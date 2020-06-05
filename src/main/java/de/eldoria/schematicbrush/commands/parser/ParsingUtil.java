package de.eldoria.schematicbrush.commands.parser;

import java.util.ArrayList;
import java.util.List;
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
}
