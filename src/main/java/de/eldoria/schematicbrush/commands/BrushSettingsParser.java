package de.eldoria.schematicbrush.commands;

import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.Util;
import de.eldoria.schematicbrush.brush.BrushConfig;
import de.eldoria.schematicbrush.brush.BrushSettings;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Flip;
import de.eldoria.schematicbrush.util.Placement;
import de.eldoria.schematicbrush.util.Rotation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BrushSettingsParser {
    private static final String name = ".+?)";
    private static final String endings = "(\\s|:|@|!|$)";

    /**
     * Pattern to use a name or a regex
     */
    private static final Pattern namePattern = Pattern.compile("^(" + name + endings);

    /**
     * Pattern to use schematics inside a directory
     */
    private static final Pattern directoryPattern = Pattern.compile("^(\\$" + name + endings);

    /**
     * Pattern to use a brush preset
     */
    private static final Pattern presetPattern = Pattern.compile("^&(" + name + endings);

    /**
     * Pattern to detect the rotation
     */
    private static final Pattern rotationPattern = Pattern.compile("@(0|90|180|270|\\*)" + endings);
    /**
     * Pattern to detect the flip
     */
    private static final Pattern flipPattern = Pattern.compile("!(NS|SN|WO|OW|N|S|W|E|\\*)" + endings, Pattern.CASE_INSENSITIVE);
    /**
     * Pattern to detect the weight
     */
    private static final Pattern weightPattern = Pattern.compile(":([0-9]{1,3}|\\*)" + endings);

    private static final Pattern yOffset = Pattern.compile("-(?:yoff)|(?:yoffset)|(?:y):-?([0-9]{1,3})", Pattern.CASE_INSENSITIVE);
    private static final Pattern placement = Pattern.compile("-(?:place)|(?:placement)|(?:p):([a-zA-Z]+?)", Pattern.CASE_INSENSITIVE);

    public static Optional<BrushSettings> parseBrush(Player player, Plugin plugin, SchematicCache schematicCache,
                                                     String[] args) {
        Optional<BrushSettings.Builder> brushSettings = buildBrush(player, args, plugin, schematicCache);

        // Check if somethin went wrong while creating the brush.
        if (brushSettings.isEmpty()) return Optional.empty();

        return buildBrushSettings(player, plugin, schematicCache, brushSettings.get(), args);
    }

    public static Optional<BrushSettings> buildBrushSettings(Player player, Plugin plugin, SchematicCache schematicCache,
                                                             BrushSettings.Builder brushSettingsBuilder, String[] args) {
        List<String> strings = Arrays.asList(args);

        if (Util.arrayContains(args, "-includeair", "-incair", "-a")) {
            brushSettingsBuilder.includeAir(true);
        }

        if (Util.arrayContains(args, "-replaceall", "-repla", "-r")) {
            brushSettingsBuilder.includeAir(true);
        }
        Matcher matcher = Util.findInArray(args, yOffset);

        if (matcher != null) {
            String value = matcher.group(1);
            int offset;
            try {
                offset = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                MessageSender.sendError(player, "Invalid offset.");
                return Optional.empty();
            }
            brushSettingsBuilder.withYOffset(offset);
        }

        matcher = Util.findInArray(args, placement);

        if (matcher != null) {
            String value = matcher.group(1);
            Placement.asPlacement(value);
            int offset;
            try {
                offset = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                MessageSender.sendError(player, "Invalid placement.");
                return Optional.empty();
            }
            brushSettingsBuilder.withYOffset(offset);
        }

        return Optional.ofNullable(brushSettingsBuilder.build());

    }

    private static Optional<BrushSettings.Builder> buildBrush(Player player, String[] settingsString, Plugin plugin,
                                                              SchematicCache schematicCache) {
        List<Schematic> schematics;

        List<String> brushStrings = Arrays.stream(settingsString)
                .filter(s -> !s.startsWith("-"))
                .collect(Collectors.toList());

        BrushSettings.Builder brushSettingsBuilder = BrushSettings.newBrushSettingsBuilder();

        // Build Brush with all provided brush strings
        for (String brushString : brushStrings) {

            // Check if its a name or regex lookup
            Matcher nameMatcher = namePattern.matcher(brushString);
            if (nameMatcher.find()) {
                Optional<BrushConfig> brushConfig = buildBrushConfig(player, brushString, schematicCache);
                if (brushConfig.isEmpty()) {
                    MessageSender.sendError(player, brushString + " is invalid");
                    return Optional.empty();
                }
                brushSettingsBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a directory lookup
            nameMatcher = directoryPattern.matcher(brushString);
            if (nameMatcher.find()) {
                Optional<BrushConfig> brushConfig = buildBrushConfig(player, brushString, schematicCache);
                if (brushConfig.isEmpty()) {
                    MessageSender.sendError(player, brushString + " is invalid");
                    return Optional.empty();
                }
                brushSettingsBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a preset
            nameMatcher = presetPattern.matcher(brushString);
            if (nameMatcher.find()) {
                String presetName = nameMatcher.group(1);
                List<String> brushConfigs = plugin.getConfig().getStringList("presets." + presetName);
                if (brushConfigs.isEmpty()) return Optional.empty();

                for (String settings : brushConfigs) {
                    Optional<BrushConfig> config = buildBrushConfig(player, settings, schematicCache);
                    if (config.isEmpty()) {
                        MessageSender.sendError(player, brushString + " is invalid");
                        return Optional.empty();
                    }

                    brushSettingsBuilder.addBrush(config.get());
                }
            }
        }

        return Optional.of(brushSettingsBuilder);
    }

    public static Optional<BrushConfig> buildBrushConfig(Player player, String settingsString, SchematicCache schematicCache) {
        BrushConfig.Builder builder = null;

        Matcher matcher = namePattern.matcher(settingsString);

        List<Schematic> schematics = Collections.emptyList();

        // Check if its a name or regex lookup
        if (matcher.find()) {
            String name = matcher.group(1);
            schematics = schematicCache.getSchematicsByName(name);
            builder = new BrushConfig.Builder(settingsString);
        }

        // Check if its a directory lookup
        matcher = directoryPattern.matcher(settingsString);
        if (matcher.find()) {
            String name = matcher.group(1);
            schematics = schematicCache.getSchematicsByDirectory(name);
            builder = new BrushConfig.Builder(settingsString);
        }


        // If no builder was initialized the expession is invalid.
        if (builder == null) {
            player.sendMessage("Invalid name type.");
            return Optional.empty();
        }

        builder.withSchematics(schematics);

        // Read rotation
        matcher = rotationPattern.matcher(settingsString);
        if (matcher.find()) {
            String value = matcher.group(1);
            builder.withRotation(Rotation.asRotation(value));
        } else if (settingsString.contains("@")) {
            MessageSender.sendError(player, "Invalid rotation!");
            return Optional.empty();
        }

        // Read flip
        matcher = flipPattern.matcher(settingsString);
        if (matcher.find()) {
            String value = matcher.group(1);
            builder.withFlip(Flip.asFlip(value));
        } else if (settingsString.contains("!")) {
            MessageSender.sendError(player, "Invalid flip!");
            return Optional.empty();
        }

        // Read weight
        matcher = weightPattern.matcher(settingsString);
        if (matcher.find()) {
            String value = matcher.group(1);
            if ("*".equals(value)) {
                builder.withWeight(-1);
            } else {
                int weight = Integer.parseInt(value);
                if (weight > 100 || weight < 0) {
                    MessageSender.sendError(player, "Invalid weight!");
                    return Optional.empty();
                }
                builder.withWeight(weight);
            }
        } else if (settingsString.contains(":")) {
            MessageSender.sendError(player, "Invalid weight!");
            return Optional.empty();
        }


        return Optional.ofNullable(builder.build());
    }

}
