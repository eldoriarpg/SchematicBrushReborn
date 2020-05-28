package de.eldoria.schematicbrush.commands;

import de.eldoria.schematicbrush.Util;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class TabUtil {
    private final String[] INCLUDE_AIR = {"-includeair", "-incair", "-a"};
    private final String[] REPLACE_ALL = {"-replaceAll", "-repla", "-r"};

    private final String[] Y_OFFSET = {"-yoffset:", "-yoff:", "-y:"};
    private final String[] PLACEMENT = {"-placement:", "-place:", "-p:"};


    private final String[] SMALL_FLAGS = {INCLUDE_AIR[0], REPLACE_ALL[0], Y_OFFSET[0], PLACEMENT[0]};
    private final String[] FLAGS = Util.combineArrays(INCLUDE_AIR, REPLACE_ALL, Y_OFFSET, PLACEMENT);

    private final String[] PLACEMENT_TYPES = {"middle", "bottom", "top", "drop", "raise"};

    private final String[] FLIP_TYPES = {"NS", "EW", "*"};
    private final String[] ROTATION_TYPES = {"0", "90", "180", "270", "*"};

    private final char[] MARKER = {':', '@', '!', '^', '$', '&'};

    public List<String> getBrushSyntax(String arg, SchematicCache cache, Plugin plugin) {
        Optional<Character> brushArgumentMarker = getBrushArgumentMarker(arg);

        if (brushArgumentMarker.isEmpty()) {
            List<String> matchingSchematics = cache.getMatchingSchematics(arg, 7);
            matchingSchematics.add("<name>@rotation!flip:weight");
            return matchingSchematics;
        }

        switch (brushArgumentMarker.get()) {
            case ':':
                return List.of("@", "!", "@rotation!flip", "<1-999>");
            case '!': {
                if (endingWithInArray(arg, FLIP_TYPES)) {
                    return List.of("@", ":", "@rotation:weight");
                }
                return List.of(FLIP_TYPES);
            }
            case '@':
                if (endingWithInArray(arg, FLIP_TYPES)) {
                    return List.of("!", ":", "!flip:weight");
                }
                return List.of(ROTATION_TYPES);
            case '^':
                return List.of("<regex>@rotation!flip:weight");
            case '$':
                List<String> matchingDirectories = cache.getMatchingDirectories(arg.substring(1), 7);
                matchingDirectories.add("<regex>@rotation!flip:weight");
                return matchingDirectories;
            case '&':
                return getPresets(arg.substring(1), plugin, 8);
        }
        return Collections.emptyList();
    }

    public boolean isFlag(String arg) {
        return arg.startsWith("-");
    }

    public List<String> getFlagComplete(String arg) {
        if (Util.arrayContains(PLACEMENT, arg)) {
            String[] split = arg.split(":");
            if (split.length == 0) {
                return List.of(PLACEMENT_TYPES);
            } else {
                return startingWithInArray(split[1], PLACEMENT_TYPES);
            }
        }

        if (Util.arrayContains(Y_OFFSET, arg)) {
            return List.of("<number>");
        }

        if ("-".equals(arg)) {
            return List.of(SMALL_FLAGS);
        }

        return startingWithInArray(arg, FLAGS);
    }

    public List<String> startingWithInArray(String string, String[] array) {
        return Arrays.stream(array).filter(e -> e.startsWith(string)).collect(Collectors.toList());
    }

    public boolean endingWithInArray(String string, String[] array) {
        return Arrays.stream(array).anyMatch(string::endsWith);
    }

    private Optional<Character> getBrushArgumentMarker(String string) {
        for (int i = string.length() - 1; i >= 0; i--) {
            char c = string.charAt(i);
            if (Util.arrayContains(MARKER, c)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
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
        List<String> strings = startingWithInArray(arg, presets.getKeys(false).toArray(array));
        return strings.subList(0, Math.min(strings.size(), count));
    }
}
