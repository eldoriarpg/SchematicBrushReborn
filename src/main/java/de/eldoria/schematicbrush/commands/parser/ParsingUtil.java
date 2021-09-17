package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.eldoutilities.container.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

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
        String result = modifier;
        result = RANDOM.matcher(result).replaceAll(":*");
        result = FLIP.matcher(result).replaceAll("!");
        result = ROTATION.matcher(result).replaceAll("@");
        return WEIGHT.matcher(result).replaceAll(":");
    }

    public static String parseToLegacySelector(String selector) {
        String result = selector;
        result = DIRECTORY.matcher(result).replaceAll("\\$");
        result = PRESET.matcher(result).replaceAll("&");
        return REGEX.matcher(result).replaceAll("\\^");
    }

    public static String[] parseToLegacySyntax(String[] args) {
        List<String> parsedInput = new ArrayList<>();
        boolean open = false;

        StringBuilder argumentBuilder = new StringBuilder();
        for (String arg : args) {
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
            String stripped = value.substring(1, value.length() - 1);
            if (stripped.contains(":")) {
                String[] split = stripped.split(":");
                Optional<T> min = parser.apply(split[0]);
                Optional<T> max = parser.apply(split[1]);
                if (!(min.isPresent() && max.isPresent())) {
                    return new ParseResult<>(Collections.emptyList(), ParseResultType.NONE);
                }
                return new ParseResult<>(Arrays.asList(min.get(), max.get()), ParseResultType.RANGE);
            }
            if (stripped.contains(",")) {
                String[] entries = stripped.split(",");
                List<T> results = new ArrayList<>();
                for (String val : entries) {
                    Optional<T> optional = parser.apply(val);
                    if (!optional.isPresent()) {
                        return new ParseResult<>(Collections.emptyList(), ParseResultType.NONE);
                    }
                    results.add(optional.get());
                }
                return new ParseResult<>(results, ParseResultType.LIST);
            }
        } else {
            Optional<T> optionOffset = parser.apply(value);
            if (!optionOffset.isPresent()) {
                return new ParseResult<>(Collections.emptyList(), ParseResultType.NONE);
            }
            return new ParseResult<>(Collections.singletonList(optionOffset.get()), ParseResultType.FIXED);
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
