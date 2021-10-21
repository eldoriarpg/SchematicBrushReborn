package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.eldoutilities.container.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

@Deprecated(forRemoval = true)
public final class ParsingUtil {
    // Legacy pattern parser
    private static final Pattern FLIP = Pattern.compile("-flip:|-f:", Pattern.CASE_INSENSITIVE);
    private static final Pattern ROTATION = Pattern.compile("-rotate:|-r:", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEIGHT = Pattern.compile("-weight:|-w:", Pattern.CASE_INSENSITIVE);
    private static final Pattern RANDOM = Pattern.compile(":random|:r", Pattern.CASE_INSENSITIVE);
    private static final Pattern DIRECTORY = Pattern.compile("dir:|d:", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRESET = Pattern.compile("preset:|p:", Pattern.CASE_INSENSITIVE);
    private static final Pattern REGEX = Pattern.compile("regex:|r:", Pattern.CASE_INSENSITIVE);

    private ParsingUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String parseToLegacyModifier(String modifier) {
        var result = modifier;
        result = RANDOM.matcher(result).replaceAll(":*");
        result = FLIP.matcher(result).replaceAll("!");
        result = ROTATION.matcher(result).replaceAll("@");
        return WEIGHT.matcher(result).replaceAll(":");
    }

    public static String parseToLegacySelector(String selector) {
        var result = selector;
        result = DIRECTORY.matcher(result).replaceAll("\\$");
        result = PRESET.matcher(result).replaceAll("&");
        return REGEX.matcher(result).replaceAll("\\^");
    }

    public static String[] parseToLegacySyntax(String[] args) {
        List<String> parsedInput = new ArrayList<>();
        var open = false;

        var argumentBuilder = new StringBuilder();
        for (var arg : args) {
            // If a string starts and ends with a double quote we assume, that a new schematic is defined inside
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                parsedInput.add(parseToLegacySelector(arg.substring(1, arg.length() - 1)));
                continue;
            }
            // If a string starts with a double quote we assume, that a new schematic set will be defined and open the section
            if (arg.startsWith("\"")) {
                open = true;
                argumentBuilder.append(parseToLegacySelector(arg.substring(1)));
                continue;
            }

            // If a string starts with a double quote we assume, that a new schematic set was defined and close the section
            if (arg.endsWith("\"")) {
                open = false;
                argumentBuilder.append(parseToLegacyModifier(arg.substring(0, arg.length() - 1)));
                parsedInput.add(argumentBuilder.toString());
                argumentBuilder.setLength(0);
                continue;
            }

            // if the current section is not open we just add the input. Could be a flagt or legacy syntax
            if (!open) {
                parsedInput.add(arg);
                continue;
            }
            // if the current section is open, we keep appending all arguments.
            argumentBuilder.append(parseToLegacyModifier(arg));
        }
        return parsedInput.toArray(new String[0]);
    }

    public static <T> ParseResult<T> parseValue(String value, Function<String, Optional<T>> parser) {
        if ("*".equals(value)) return new ParseResult<>(Collections.emptyList(), ParseResultType.RANDOM);

        if (value.startsWith("[") && value.endsWith("]")) {
            var stripped = value.substring(1, value.length() - 1);
            if (stripped.contains(":")) {
                var split = stripped.split(":");
                var min = parser.apply(split[0]);
                var max = parser.apply(split[1]);
                if (!(min.isPresent() && max.isPresent())) {
                    return new ParseResult<>(Collections.emptyList(), ParseResultType.NONE);
                }
                return new ParseResult<>(Arrays.asList(min.get(), max.get()), ParseResultType.RANGE);
            }
            if (stripped.contains(",")) {
                var entries = stripped.split(",");
                List<T> results = new ArrayList<>();
                for (var val : entries) {
                    var optional = parser.apply(val);
                    if (optional.isEmpty()) {
                        return new ParseResult<>(Collections.emptyList(), ParseResultType.NONE);
                    }
                    results.add(optional.get());
                }
                return new ParseResult<>(results, ParseResultType.LIST);
            }
        } else {
            return parser.apply(value)
                    .map(t -> new ParseResult<>(Collections.singletonList(t), ParseResultType.FIXED))
                    .orElseGet(() -> new ParseResult<>(Collections.emptyList(), ParseResultType.NONE));
        }
        return new ParseResult<>(Collections.emptyList(), ParseResultType.NONE);
    }

    public enum ParseResultType {
        NONE, LIST, RANDOM, RANGE, FIXED
    }

    public static class ParseResult<T> {
        private final List<T> results;
        private final ParseResultType type;

        public ParseResult(List<T> results, ParseResultType type) {
            this.results = results;
            this.type = type;
        }

        public ParseResultType type() {
            return type;
        }

        T result() {
            return results.get(0);
        }

        Pair<T, T> range() {
            return Pair.of(results.get(0), results.get(1));
        }

        List<T> results() {
            return results;
        }
    }
}
