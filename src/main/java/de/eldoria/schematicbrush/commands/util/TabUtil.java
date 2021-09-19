package de.eldoria.schematicbrush.commands.util;

import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.eldoutilities.utils.ArrayUtil;
import de.eldoria.eldoutilities.utils.TextUtil;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TabUtil {
    private static final String[] INCLUDE_AIR = {"-includeair", "-incair", "-a"};
    private static final String[] REPLACE_ALL = {"-replaceAll", "-repla", "-r"};

    private static final String[] Y_OFFSET = {"-yoffset:", "-yoff:", "-y:"};
    private static final String[] PLACEMENT = {"-placement:", "-place:", "-p:"};


    private static final String[] SMALL_FLAGS = {INCLUDE_AIR[0], REPLACE_ALL[0], Y_OFFSET[0], PLACEMENT[0]};
    private static final String[] FLAGS = ArrayUtil.combineArrays(INCLUDE_AIR, REPLACE_ALL, Y_OFFSET, PLACEMENT);

    private static final String[] PLACEMENT_TYPES = {"middle", "bottom", "top", "drop", "raise", "original"};

    private static final String[] FLIP_TYPES = {"N", "W", "NS", "WE", "*"};
    private static final String[] ROTATION_TYPES = {"0", "90", "180", "270", "*"};

    private static final String[] SELECTOR_TYPE = {"<name>", "dir:", "regex:", "preset:"};
    private static final String[] SELECTOR_TYPE_MATCH = {"dir:", "d:", "regex:", "r:", "preset:", "p:"};

    private static final String[] MODIFIERS = {"-flip:", "-rotate:", "-weight:", "-f:", "-r:", "-w:"};

    private static final String[] ROTATION = {"90", "180", "270", "random"};
    private static final String[] FLIP = {"N", "E", "random"};

    private static final char[] MARKER = {':', '@', '!', '^', '$', '&'};

    private TabUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static List<String> getSchematicSetSyntax(Player player, String[] args, SchematicCache cache, Config config) {
        var quoteCount = TextUtil.countChars(String.join(" ", args), '\"');
        var last = args[args.length - 1];
        if (quoteCount % 2 == 0) {
            return getLegacySchematicSetSyntax(player, last, cache, config);
        }

        return getSchematicSetSyntax(player, last, cache, config);
    }

    private static List<String> getSchematicSetSyntax(Player player, String arg, SchematicCache cache, Config config) {
        if (arg.startsWith("\"")) {
            if ("\"".equals(arg)) {
                return prefixStrings(Arrays.asList(SELECTOR_TYPE), "\"");
            }

            var selector = arg.substring(1).toLowerCase();
            var split = arg.split(":");

            if (selector.startsWith("dir:") || selector.startsWith("d:")) {
                var matchingDirectories = cache.getMatchingDirectories(player, split.length == 1 ? "" : split[1].split("#")[0], 50);
                return prefixStrings(matchingDirectories, split[0] + ":");
            }

            if (selector.startsWith("preset:") || selector.startsWith("p:")) {
                var presets = getPresets(split.length == 1 ? "" : split[1], 50, config);
                return prefixStrings(presets, split[0] + ":");
            }

            if (selector.startsWith("regex:") || selector.startsWith("r:")) {
                return Collections.singletonList(selector + "<regex>");
            }

            var matches = ArrayUtil.startingWithInArray(selector, SELECTOR_TYPE).collect(Collectors.toList());
            matches.addAll(cache.getMatchingSchematics(player, selector, 50));
            Collections.reverse(matches);
            return prefixStrings(matches, "\"");
        }

        var split = arg.split(":");

        if (arg.startsWith("-rotate:") || arg.startsWith("-r:")) {
            return completeArrayFlags(arg, ROTATION_TYPES);
        }

        if (arg.startsWith("-flip:") || arg.startsWith("-f:")) {
            return completeArrayFlags(arg, FLIP_TYPES);
        }

        if (arg.startsWith("-weight:") || arg.startsWith("-w:")) {
            return Collections.singletonList(split[0] + ":<number>");
        }

        return ArrayUtil.startingWithInArray(arg, MODIFIERS).collect(Collectors.toList());
    }

    /**
     * Get the brush syntax for the current entry.
     *
     * @param arg   argument which should be completed
     * @param cache cache for schematic lookup
     * @return a list of possible completions
     */
    private static List<String> getLegacySchematicSetSyntax(Player player, String arg, SchematicCache cache, Config config) {
        var brushArgumentMarker = getBrushArgumentMarker(arg);
        var firstMarker = getBrushArgumentMarker(arg, true);

        if (arg.isEmpty()) {
            return Arrays.asList("<name>@rotation!flip:weight",
                    "$<directory>@rotation!flip:weight",
                    "&<presetname>",
                    "^<regex>@rotation!flip:weight");
        }

        if (brushArgumentMarker.isEmpty()) {
            var matchingSchematics = cache.getMatchingSchematics(player, arg, 50);
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
            case '!':
                return completeLegacyArrayFlags(arg, FLIP_TYPES);
            case '@':
                return completeLegacyArrayFlags(arg, ROTATION_TYPES);
            case '^':
                if (firstMarker.isEmpty() || firstMarker.get() != '$') {
                    return Arrays.asList("^<regex>@rotation!flip:weight", arg + "@", arg + "!", arg + ":");
                }
            case '$': {
                var directory = arg.substring(1).split("#")[0];
                var matchingDirectories = cache.getMatchingDirectories(player, directory, 50);
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
                var preset = arg.substring(1);
                var presets = getPresets(preset, 50, config);
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
        if (TextUtil.countChars(String.join(" ", arg), '"') % 2 == 0) {
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
        if (ArrayUtil.stringStartingWithValueInArray(flag, PLACEMENT)) {
            var split = flag.split(":");
            if (split.length == 1) {
                return prefixStrings(Arrays.asList(PLACEMENT_TYPES), split[0] + ":");
            }
            return ArrayUtil.startingWithInArray(split[1], PLACEMENT_TYPES)
                    .map(t -> split[0] + ":" + t)
                    .collect(Collectors.toList());
        }

        if (ArrayUtil.stringStartingWithValueInArray(flag, Y_OFFSET)) {
            var split = flag.split(":", 2);
            if (split[1].isEmpty()) {
                return Arrays.asList(flag + "<number>", flag + "[<min>:<max>]", flag + "[<num1>,<num2>,...]");
            }
            var value = split[1];
            if (value.startsWith("[")) {
                if (value.endsWith("]")) {
                    return Collections.singletonList(flag);
                }

                if (!value.contains(":") && !value.contains(",")) {
                    return Arrays.asList(flag + ":<max>]", flag + ",<num2>,...]");
                }
                if (value.contains(":")) {
                    if (value.endsWith(":")) {
                        return Collections.singletonList(flag + "<max>]");
                    }
                    return Collections.singletonList(flag + "]");
                }
                if (value.contains(",")) {
                    if (value.endsWith(",")) {
                        return Collections.singletonList(flag + "<num>]");
                    }
                    return Arrays.asList(flag + "]", flag + ",<num>]");
                }
            }
            return Collections.singletonList(split[0] + ":" + "<number>");
        }

        if ("-".equals(flag)) {
            return Arrays.asList(SMALL_FLAGS);
        }

        return ArrayUtil.startingWithInArray(flag, FLAGS).collect(Collectors.toList());
    }


    /**
     * Get the last brush argument marker in a string.
     *
     * @param input string to check
     * @return optional argument marker if one is found.
     */
    private static Optional<Character> getBrushArgumentMarker(String input) {
        return getBrushArgumentMarker(input, false);
    }

    /**
     * Get the last brush argument marker in a string.
     *
     * @param input   string to check
     * @param reverse true if the first argument marker should be returned
     * @return optional argument marker if one is found.
     */
    private static Optional<Character> getBrushArgumentMarker(String input, boolean reverse) {
        if (reverse) {
            for (var i = 0; i < input.length(); i++) {
                var c = input.charAt(i);
                if (ArrayUtil.arrayContains(MARKER, c)) {
                    return Optional.of(c);
                }
            }
        } else {
            for (var i = input.length() - 1; i >= 0; i--) {
                var c = input.charAt(i);
                if (ArrayUtil.arrayContains(MARKER, c)) {
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get the string from end to the last argument marker.
     *
     * @param input string to check
     * @return substring between end and last argument marker.
     */
    private static String getBrushArgumentStringToLastMarker(String input) {
        for (var i = input.length() - 1; i >= 0; i--) {
            var c = input.charAt(i);
            if (ArrayUtil.arrayContains(MARKER, c)) {
                return input.substring(0, i + 1);
            }
        }
        return input;
    }

    /**
     * Get a list of all preset names from the config which match a string
     *
     * @param arg    argument to check
     * @param count  number of max returned preset names
     * @param config config
     * @return list of matchin presets of length count or shorter.
     */
    public static List<String> getPresets(String arg, int count, Config config) {
        var complete = TabCompleteUtil.complete(arg, config.getPresetName());
        return complete.subList(0, Math.min(count, complete.size()));
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
        var explanation = new StringBuilder();
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

    private static List<String> completeLegacyArrayFlags(String arg, String[] values) {
        var pre = getBrushArgumentStringToLastMarker(arg);
        var val = arg.replace(pre, "");
        if (val.startsWith("[")) {
            if (val.endsWith("]")) {
                return getMissingSchematicSetArguments(arg);
            }

            if (val.contains(",")) {
                if (val.endsWith(",")) {
                    return prefixStrings(Arrays.asList(values), arg);
                }
                if (ArrayUtil.endingWithInArray(arg, values)) {
                    return Arrays.asList(arg + "]", arg + ",");
                }
                var split = val.split(",");
                var end = split[split.length - 1];
                var join = String.join(",", Arrays.copyOfRange(split, 0, split.length - 1));
                return prefixStrings(TabCompleteUtil.complete(end, values), join + ",");
            }
            if (ArrayUtil.endingWithInArray(arg, values)) {
                return Arrays.asList(arg + "]", arg + ",");
            }
            return prefixStrings(TabCompleteUtil.complete(val.replace("[", ""), values), pre + "[");
        }
        if (ArrayUtil.endingWithInArray(arg, values)) {
            return getMissingSchematicSetArguments(arg);
        }
        var strings = prefixStrings(Arrays.asList(values), arg);
        strings.add(pre + "[");
        return strings;
    }

    private static List<String> completeArrayFlags(String arg, String[] values) {
        var split = arg.split(":");
        if (split.length == 1) {
            var strings = prefixStrings(Arrays.asList(values), split[0] + ":");
            strings.add(arg + "[");
            return strings;
        }

        var val = split[1];

        if (val.startsWith("[")) {
            if (val.endsWith("]")) {
                return Collections.emptyList();
            }

            if (val.contains(",")) {
                if (val.endsWith(",")) {
                    return prefixStrings(Arrays.asList(values), arg);
                }
                if (ArrayUtil.endingWithInArray(arg, values)) {
                    return Arrays.asList(arg + "]", arg + ",");
                }
                var args = val.split(",");
                var end = args[args.length - 1];
                var join = String.join(",", Arrays.copyOfRange(args, 0, args.length - 1));
                return prefixStrings(TabCompleteUtil.complete(end, values), join + ",");
            }
            if (ArrayUtil.endingWithInArray(arg, values)) {
                return Arrays.asList(arg + "]", arg + ",");
            }
            return prefixStrings(Arrays.asList(values), arg);
        }
        if (ArrayUtil.endingWithInArray(arg, values)) {
            return getMissingSchematicSetArguments(arg);
        }

        var strings = prefixStrings(Arrays.asList(values), arg);
        strings.add(arg + "[");
        return strings;
    }
}
