package de.eldoria.schematicbrush.commands.util;

import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.ArrayUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.eldoria.schematicbrush.util.TextUtil.countChars;

public final class TabUtil {
    private static final String[] INCLUDE_AIR = {"-includeair", "-incair", "-a"};
    private static final String[] REPLACE_ALL = {"-replaceAll", "-repla", "-r"};

    private static final String[] Y_OFFSET = {"-yoffset:", "-yoff:", "-y:"};
    private static final String[] PLACEMENT = {"-placement:", "-place:", "-p:"};


    private static final String[] SMALL_FLAGS = {INCLUDE_AIR[0], REPLACE_ALL[0], Y_OFFSET[0], PLACEMENT[0]};
    private static final String[] FLAGS = ArrayUtil.combineArrays(INCLUDE_AIR, REPLACE_ALL, Y_OFFSET, PLACEMENT);

    private static final String[] PLACEMENT_TYPES = {"middle", "bottom", "top", "drop", "raise"};

    private static final String[] FLIP_TYPES = {"N", "W", "NS", "WE", "*"};
    private static final String[] ROTATION_TYPES = {"90", "180", "270", "*"};

    private static final String[] SELECTOR_TYPE = {"dir:", "regex:", "preset:"};
    private static final String[] SELECTOR_TYPE_MATCH = {"directory:", "d:", "regex:", "r:", "preset:", "p:"};

    private static final String[] MODIFIERS = {"-flip:", "-rotate:", "-weight:", "-f:", "-r:", "-w:"};

    private static final String[] ROTATION = {"90", "180", "270", "random"};
    private static final String[] FLIP = {"N", "E", "random"};

    private static final char[] MARKER = {':', '@', '!', '^', '$', '&'};

    private TabUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static List<String> getSchematicSetSyntax(String[] args, SchematicCache cache, Plugin plugin) {
        int quoteCount = countChars(String.join(" ", args), '\"');
        String last = args[args.length - 1];
        if (quoteCount % 2 == 0) {
            return getLegacySchematicSetSyntax(last, cache, plugin);
        }

        return getSchematicSetSyntax(last, cache, plugin);
    }

    private static List<String> getSchematicSetSyntax(String arg, SchematicCache cache, Plugin plugin) {
        if (arg.startsWith("\"")) {
            if ("\"".equals(arg)) {
                return prefixStrings(Arrays.asList(SELECTOR_TYPE), "\"");
            }

            String selector = arg.substring(1).toLowerCase();
            String[] split = arg.split(":");

            if (selector.startsWith("dir:") || selector.startsWith("d:")) {
                List<String> matchingDirectories = cache.getMatchingDirectories(split.length == 1 ? "" :split[1], 50);
                return prefixStrings(matchingDirectories, split[0] + ":");
            }

            if (selector.startsWith("preset:") || selector.startsWith("p:")) {
                List<String> presets = getPresets(split.length == 1 ? "" : split[1], plugin, 50);
                return prefixStrings(presets, split[0] + ":");
            }

            if (selector.startsWith("regex:") || selector.startsWith("r:")) {
                return Collections.singletonList(selector + "<regex>");
            }

            List<String> matches = startingWithInArray(selector, SELECTOR_TYPE).collect(Collectors.toList());
            matches.addAll(cache.getMatchingSchematics(selector, 50));
            Collections.reverse(matches);
            return prefixStrings(matches, "\"");
        }

        String[] split = arg.split(":");

        if (arg.startsWith("-rotate:") || arg.startsWith("-r:")) {
            if (split.length == 1) {
                return prefixStrings(Arrays.asList(ROTATION), split[0]+ ":");
            }
            return prefixStrings(startingWithInArray(split[1], ROTATION).collect(Collectors.toList()), split[0] + ":");
        }

        if (arg.startsWith("-flip:") || arg.startsWith("-f:")) {
            if (split.length == 1) {
                return prefixStrings(Arrays.asList(FLIP), split[0] + ":");
            }
            return prefixStrings(startingWithInArray(split[1], FLIP).collect(Collectors.toList()), split[0] + ":");
        }

        if (arg.startsWith("-weight:") || arg.startsWith("-w:")) {
            return Collections.singletonList(split[0] + ":<number>");
        }

        return startingWithInArray(arg, MODIFIERS).collect(Collectors.toList());
    }

    /**
     * Get the brush syntax for the current entry.
     *
     * @param arg    argument which should be completed
     * @param cache  cache for schematic lookup
     * @param plugin plugin for config access
     * @return a list of possible completions
     */
    private static List<String> getLegacySchematicSetSyntax(String arg, SchematicCache cache, Plugin plugin) {
        Optional<Character> brushArgumentMarker = getBrushArgumentMarker(arg);

        if (arg.isEmpty()) {
            return Arrays.asList("<name>@rotation!flip:weight",
                    "$<directory>@rotation!flip:weight",
                    "&<presetname>",
                    "^<regex>@rotation!flip:weight");
        }

        if (!brushArgumentMarker.isPresent()) {
            List<String> matchingSchematics = cache.getMatchingSchematics(arg, 50);
            matchingSchematics.add("<name>@rotation!flip:weight");
            if (matchingSchematics.size() == 1) {
                matchingSchematics.addAll(getMissingSchematicSetArguments(arg));
                Collections.reverse(matchingSchematics);
            }
            return matchingSchematics;
        }

        switch (brushArgumentMarker.get()) {
            case ':':
                return Arrays.asList(arg + "@", arg + "!", "@rotation!flip", arg + "<1-999>");
            case '!': {
                if (endingWithInArray(arg, FLIP_TYPES)) {
                    return getMissingSchematicSetArguments(arg);
                }
                return prefixStrings(Arrays.asList(FLIP_TYPES), getBrushArgumentStringToLastMarker(arg));
            }
            case '@':
                if (endingWithInArray(arg, ROTATION_TYPES)) {
                    return getMissingSchematicSetArguments(arg);
                }
                return prefixStrings(Arrays.asList(ROTATION_TYPES), getBrushArgumentStringToLastMarker(arg));
            case '^':
                return Arrays.asList("^<regex>@rotation!flip:weight", arg + "@", arg + "!", arg + ":");
            case '$': {
                String directory = arg.substring(1);
                List<String> matchingDirectories = cache.getMatchingDirectories(directory, 50);
                matchingDirectories = prefixStrings(matchingDirectories, "$");
                // only if a direct match is found add schematic arguments.
                if (matchingDirectories.stream().anyMatch(d -> d.equalsIgnoreCase(directory)) || directory.endsWith("*")) {
                    matchingDirectories.addAll(getMissingSchematicSetArguments(arg));
                } else {
                    matchingDirectories.add("$<directory>@rotation!flip:weight");
                }
                return matchingDirectories;
            }
            case '&': {
                String preset = arg.substring(1);
                List<String> presets = getPresets(preset, plugin, 50);
                presets = prefixStrings(presets, "&");
                if (presets.size() < 1) {
                    Collections.reverse(presets);
                } else {
                    presets.add("&<preset>");
                }
                return presets;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Checks if the argument is a brush flag.
     *
     * @param arg argument to check
     * @return true if the argument is a flag
     */
    public static boolean isFlag(String[] arg) {
        if (countChars(String.join(" ", arg), '"') % 2 == 0) {
            return arg[arg.length - 1].startsWith("-");

        }
        return false;
    }

    /**
     * Get a tab complete for a flag. This will fail if {@link #isFlag(String[])} is false.
     *
     * @param flag flag to check
     * @return list of possible completions
     */
    public static List<String> getFlagComplete(String flag) {
        if (stringStartingWithValueInArray(flag, PLACEMENT)) {
            String[] split = flag.split(":");
            if (split.length == 1) {
                return prefixStrings(Arrays.asList(PLACEMENT_TYPES), split[0] + ":");
            } else {
                return startingWithInArray(split[1], PLACEMENT_TYPES)
                        .map(t -> split[0] + ":" + t)
                        .collect(Collectors.toList());
            }
        }

        if (stringStartingWithValueInArray(flag, Y_OFFSET)) {
            return Collections.singletonList(flag + "<number>");
        }

        if ("-".equals(flag)) {
            return Arrays.asList(SMALL_FLAGS);
        }

        return startingWithInArray(flag, FLAGS).collect(Collectors.toList());
    }

    /**
     * Searches for strings, which are starting with the provided value
     *
     * @param value start to search for
     * @param array array to check
     * @return list of strings which starts with the provided value
     */
    public static Stream<String> startingWithInArray(String value, String[] array) {
        return Arrays.stream(array).filter(e -> e.startsWith(value));
    }

    /**
     * Checks if a string start with any value in a string.
     *
     * @param value value to check
     * @param array array values.
     * @return true if the value starts with any value in the array
     */
    public static boolean stringStartingWithValueInArray(String value, String[] array) {
        return Arrays.stream(array).anyMatch(value::startsWith);
    }

    /**
     * Checks if a string ends with a value in a array
     *
     * @param value value to check
     * @param array array values.
     * @return true if the value ends with any value in the array
     */
    public static boolean endingWithInArray(String value, String[] array) {
        return Arrays.stream(array).anyMatch(value::endsWith);
    }

    /**
     * Get the last brush argument marker in a string.
     *
     * @param string string to check
     * @return optional argument marker if one is found.
     */
    private static Optional<Character> getBrushArgumentMarker(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            char c = string.charAt(i);
            if (ArrayUtil.arrayContains(MARKER, c)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    /**
     * Get the string from end to the last argument marker.
     *
     * @param string string to check
     * @return substring between end and last argument marker.
     */
    private static String getBrushArgumentStringToLastMarker(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            char c = string.charAt(i);
            if (ArrayUtil.arrayContains(MARKER, c)) {
                return string.substring(0, i + 1);
            }
        }
        return string;
    }

    /**
     * Get a list of all preset names from the config which match a string
     *
     * @param arg    argument to check
     * @param plugin plugin for config lookup
     * @param count  number of max returned preset names
     * @return list of matchin presets of length count or shorter.
     */
    public static List<String> getPresets(String arg, Plugin plugin, int count) {
        ConfigurationSection presets = plugin.getConfig().getConfigurationSection("presets");
        if (presets == null) {
            return new ArrayList<>(Collections.singletonList("Preset section missing in config!"));
        }
        if (presets.getKeys(false).isEmpty()) {
            return new ArrayList<>(Collections.singletonList("No presets defined!"));
        }
        List<String> strings;
        String[] array = new String[presets.getKeys(false).size()];
        if (arg.isEmpty()) {
            strings = new ArrayList<>(presets.getKeys(false));
        } else {
            strings = startingWithInArray(arg, presets.getKeys(false).toArray(array))
                    .collect(Collectors.toList());
        }
        return strings.subList(0, Math.min(strings.size(), count));
    }

    /**
     * Appends all strings on a prefix string
     *
     * @param list   list to prefix
     * @param prefix prefix to add
     * @return list of strings which start with the prefix
     */
    private static List<String> prefixStrings(List<String> list, String prefix) {
        return list.stream().map(s -> prefix + s).collect(Collectors.toList());
    }

    /**
     * Get the missing brush argument with a explanation string.
     *
     * @param arg argument to check
     * @return list of missing arguments
     */
    private static List<String> getMissingSchematicSetArguments(String arg) {
        // A preset cant have modifiers.
        if (arg.startsWith("&")) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        StringBuilder explanation = new StringBuilder();
        if (!arg.contains("@")) {
            result.add(arg + "@");
            explanation.append("@rotation");
        }
        if (!arg.contains(":")) {
            result.add(arg + ":");
            explanation.append(":weight");
        }
        if (!arg.contains("!")) {
            result.add(arg + "!");
            explanation.append("!flip");
        }
        if (!result.isEmpty()) {
            result.add(explanation.toString());
        }
        return result;
    }

}
