package de.eldoria.schematicbrush.commands.util;

import de.eldoria.schematicbrush.util.ArrayUtil;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class TabUtil {
    private final String[] INCLUDE_AIR = {"-includeair", "-incair", "-a"};
    private final String[] REPLACE_ALL = {"-replaceAll", "-repla", "-r"};

    private final String[] Y_OFFSET = {"-yoffset:", "-yoff:", "-y:"};
    private final String[] PLACEMENT = {"-placement:", "-place:", "-p:"};


    private final String[] SMALL_FLAGS = {INCLUDE_AIR[0], REPLACE_ALL[0], Y_OFFSET[0], PLACEMENT[0]};
    private final String[] FLAGS = ArrayUtil.combineArrays(INCLUDE_AIR, REPLACE_ALL, Y_OFFSET, PLACEMENT);

    private final String[] PLACEMENT_TYPES = {"middle", "bottom", "top", "drop", "raise"};

    private final String[] FLIP_TYPES = {"N", "W","NS", "WE", "*"};
    private final String[] ROTATION_TYPES = {"0", "90", "180", "270", "*"};

    private final char[] MARKER = {':', '@', '!', '^', '$', '&'};

    public List<String> getBrushSyntax(String arg, SchematicCache cache, Plugin plugin) {
        Optional<Character> brushArgumentMarker = getBrushArgumentMarker(arg);

        if(arg.isEmpty()){
            return List.of("<name>@rotation!flip:weight",
                    "$<directory>@rotation!flip:weight",
                    "&<presetname>@rotation!flip:weight",
                    "^<regex>@rotation!flip:weight");
        }

        if (brushArgumentMarker.isEmpty()) {
            List<String> matchingSchematics = cache.getMatchingSchematics(arg, 50);
            matchingSchematics.add("<name>@rotation!flip:weight");
            if (matchingSchematics.size() == 1) {
                matchingSchematics.addAll(getMissingBrushArguments(arg));
                Collections.reverse(matchingSchematics);
            }
            return matchingSchematics;
        }

        switch (brushArgumentMarker.get()) {
            case ':':
                return List.of(arg + "@", arg + "!", "@rotation!flip", arg + "<1-999>");
            case '!': {
                if (endingWithInArray(arg, FLIP_TYPES)) {
                    return getMissingBrushArguments(arg);
                }
                return prefixStrings(List.of(FLIP_TYPES), getBrushArgumentStringToLastMarker(arg));
            }
            case '@':
                if (endingWithInArray(arg, ROTATION_TYPES)) {
                    return getMissingBrushArguments(arg);
                }
                return prefixStrings(List.of(ROTATION_TYPES), getBrushArgumentStringToLastMarker(arg));
            case '^':
                return List.of("^<regex>@rotation!flip:weight", arg + "@", arg + "!", arg + ":");
            case '$': {
                List<String> matchingDirectories = cache.getMatchingDirectories(arg.substring(1), 50);
                matchingDirectories = prefixStrings(matchingDirectories, "$");
                if (matchingDirectories.size() < 5) {
                    matchingDirectories.addAll(getMissingBrushArguments(arg));
                    Collections.reverse(matchingDirectories);
                } else {
                    matchingDirectories.add("$<directory>@rotation!flip:weight");
                }
                return matchingDirectories;
            }
            case '&': {
                List<String> presets = getPresets(arg.substring(1), plugin, 50);
                presets = prefixStrings(presets, "&");
                if (presets.size() < 5) {
                    presets.addAll(getMissingBrushArguments(arg));
                    Collections.reverse(presets);
                } else {
                    presets.add("&<preset>@rotation!flip:weight");
                }
                return presets;
            }
        }
        return Collections.emptyList();
    }

    public boolean isFlag(String arg) {
        return arg.startsWith("-");
    }

    public List<String> getFlagComplete(String arg) {
        if (stringStartingWithValueInArray(arg, PLACEMENT)) {
            String[] split = arg.split(":");
            if (split.length == 1) {
                return prefixStrings(List.of(PLACEMENT_TYPES), split[0] + ":b");
            } else {
                return startingWithInArray(split[1], PLACEMENT_TYPES)
                        .map(t -> split[0] + ":" + t)
                        .collect(Collectors.toList());
            }
        }

        if (stringStartingWithValueInArray(arg, Y_OFFSET)) {
            return List.of(arg + "<number>");
        }

        if ("-".equals(arg)) {
            return List.of(SMALL_FLAGS);
        }

        return startingWithInArray(arg, FLAGS).collect(Collectors.toList());
    }

    public Stream<String> startingWithInArray(String string, String[] array) {
        return Arrays.stream(array).filter(e -> e.startsWith(string));
    }

    public boolean stringStartingWithValueInArray(String string, String[] array) {
        return Arrays.stream(array).anyMatch(string::startsWith);
    }

    public boolean endingWithInArray(String string, String[] array) {
        return Arrays.stream(array).anyMatch(string::endsWith);
    }

    private Optional<Character> getBrushArgumentMarker(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            char c = string.charAt(i);
            if (ArrayUtil.arrayContains(MARKER, c)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    private String getBrushArgumentStringToLastMarker(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            char c = string.charAt(i);
            if (ArrayUtil.arrayContains(MARKER, c)) {
                return string.substring(0, i + 1);
            }
        }
        return string;
    }

    public List<String> getPresets(String arg, Plugin plugin, int count) {
        ConfigurationSection presets = plugin.getConfig().getConfigurationSection("presets");
        if (presets == null) {
            return List.of("Preset section missing in config!");
        }
        if (presets.getKeys(false).isEmpty()) {
            return List.of("No presets defined!");
        }
        String[] array = new String[presets.getKeys(false).size()];
        List<String> strings = startingWithInArray(arg, presets.getKeys(false).toArray(array))
                .collect(Collectors.toList());
        return strings.subList(0, Math.min(strings.size(), count));
    }

    private List<String> prefixStrings(List<String> list, String prefix) {
        return list.stream().map(s -> prefix + s).collect(Collectors.toList());
    }

    private List<String> getMissingBrushArguments(String arg) {
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
