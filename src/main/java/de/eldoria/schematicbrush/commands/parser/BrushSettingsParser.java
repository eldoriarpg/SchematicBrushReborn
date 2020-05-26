package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.Util;
import de.eldoria.schematicbrush.brush.BrushConfig;
import de.eldoria.schematicbrush.brush.BrushSelector;
import de.eldoria.schematicbrush.brush.BrushSettings;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Placement;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class BrushSettingsParser {
    private final Pattern Y_OFFSET = Pattern.compile("-(?:yoff)|(?:yoffset)|(?:y):-?([0-9]{1,3})", Pattern.CASE_INSENSITIVE);
    private final Pattern PLACEMENT = Pattern.compile("-(?:place)|(?:placement)|(?:p):([a-zA-Z]+?)", Pattern.CASE_INSENSITIVE);

    public Optional<BrushSettings> parseBrush(Player player, Plugin plugin, SchematicCache schematicCache,
                                                     String[] args) {
        // Remove brush settings from arguments.
        List<String> brushes = Arrays.stream(args).filter(c -> !c.startsWith("-")).collect(Collectors.toList());

        Optional<BrushSettings.Builder> brushSettings = buildBrushes(player, brushes, plugin, schematicCache);

        // Check if somethin went wrong while creating the brush.
        if (brushSettings.isEmpty()) return Optional.empty();

        return buildBrushSettings(player, brushSettings.get(), args);
    }

    /**
     * Build brushes from one or more brush argument strings
     *
     * @param player          executor of the brush
     * @param settingsStrings one or more brushes
     * @param plugin          plugin instance
     * @param schematicCache  schematic cache instance
     * @return A optional, which returns a unconfigured {@link BrushSettings.Builder} with brushes already set
     * or empty if a brush string could not be parsed
     */
    private Optional<BrushSettings.Builder> buildBrushes(Player player, List<String> settingsStrings, Plugin plugin,
                                                                SchematicCache schematicCache) {
        BrushSettings.Builder builder = BrushSettings.newBrushSettingsBuilder();

        for (String settingsString : settingsStrings) {
            // Get the brush type
            Optional<BrushArgumentParser.BrushType> optionalBrushType = BrushArgumentParser.getBrushType(settingsString);
            if (optionalBrushType.isEmpty()) {
                MessageSender.sendError(player, "Invalid schematic selector");
                return Optional.empty();
            }

            BrushArgumentParser.BrushType brushType = optionalBrushType.get();

            // Check if its a name or regex lookup
            if (brushType.getSelectorType() == BrushSelector.REGEX) {
                Optional<BrushConfig> brushConfig = buildBrushConfig(player, brushType, settingsString, schematicCache);
                if (brushConfig.isEmpty()) {
                    return Optional.empty();
                }
                builder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a directory lookup
            if (brushType.getSelectorType() == BrushSelector.DIRECTORY) {
                Optional<BrushConfig> brushConfig = buildBrushConfig(player, brushType, settingsString, schematicCache);
                if (brushConfig.isEmpty()) {
                    MessageSender.sendError(player, settingsString + " is invalid");
                    return Optional.empty();
                }
                builder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a preset
            if (brushType.getSelectorType() == BrushSelector.PRESET) {
                if (!plugin.getConfig().contains("presets." + brushType.getSelectorValue())) {
                    MessageSender.sendError(player, "This brush preset does not exist.");
                }

                // Get list of brush arguments.
                List<String> brushConfigs = plugin.getConfig().getStringList("presets." + brushType.getSelectorValue());

                if (brushConfigs.isEmpty()) {
                    MessageSender.sendError(player, "This preset does not contain any brushes");
                    return Optional.empty();
                }

                for (String settings : brushConfigs) {
                    optionalBrushType = BrushArgumentParser.getBrushType(settings);

                    if (optionalBrushType.isEmpty()) {
                        MessageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    // Block if a preset is used in a preset to avoid recursive calls.
                    if (optionalBrushType.get().getSelectorType() == BrushSelector.PRESET) {
                        MessageSender.sendError(player, "Presets are now allowed in presets.");
                        return Optional.empty();
                    }

                    Optional<BrushConfig> config = buildBrushConfig(player, brushType, settings, schematicCache);
                    if (config.isEmpty()) {
                        MessageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    builder.addBrush(config.get());
                }
            }
        }
        return Optional.empty();
    }

    public Optional<BrushConfig> buildBrushConfig(Player player, BrushArgumentParser.BrushType type, String settingsString, SchematicCache schematicCache) {
        BrushConfig.Builder builder = null;

        List<Schematic> schematics = Collections.emptyList();

        // Check if its a name or regex lookup
        if (type.getSelectorType() == BrushSelector.REGEX) {
            schematics = schematicCache.getSchematicsByName(type.getSelectorValue());
            builder = new BrushConfig.Builder(settingsString);
        }

        // Check if its a directory lookup
        if (type.getSelectorType() == BrushSelector.DIRECTORY) {
            schematics = schematicCache.getSchematicsByDirectory(type.getSelectorValue());
            builder = new BrushConfig.Builder(settingsString);
        }


        // If no builder was initialized the expession is invalid.
        if (builder == null) {
            player.sendMessage("Invalid name type.");
            return Optional.empty();
        }

        builder.withSchematics(schematics);

        BrushArgumentParser.BrushConfigValues brushConfigValues = BrushArgumentParser.getBrushValues(settingsString);


        // Read rotation
        if (brushConfigValues.getRotation() != null) {
            builder.withRotation(brushConfigValues.getRotation());
        } else if (settingsString.contains("@")) {
            MessageSender.sendError(player, "Invalid rotation!");
            return Optional.empty();
        }

        // Read flip
        if (brushConfigValues.getFlip() != null) {
            builder.withFlip(brushConfigValues.getFlip());
        } else if (settingsString.contains("!")) {
            MessageSender.sendError(player, "Invalid flip!");
            return Optional.empty();
        }

        // Read weight
        if (brushConfigValues.getWeight() != null) {
            builder.withWeight(brushConfigValues.getWeight());
        } else if (settingsString.contains(":")) {
            MessageSender.sendError(player, "Invalid weight!");
            return Optional.empty();
        }


        return Optional.ofNullable(builder.build());
    }

    /**
     * Build a new Brush from a {@link BrushSettings.Builder}
     *
     * @param player               executor of the brush
     * @param brushSettingsBuilder Unconfigures builder for brush settings
     * @param args                 arguments of the brush
     * @return optional configured brush settings object or empty if something could not be parsed
     */
    public Optional<BrushSettings> buildBrushSettings(Player player, BrushSettings.Builder brushSettingsBuilder,
                                                             String[] args) {
        List<String> strings = Arrays.asList(args);

        if (Util.arrayContains(args, "-includeair", "-incair", "-a")) {
            brushSettingsBuilder.includeAir(true);
        }

        if (Util.arrayContains(args, "-replaceall", "-repla", "-r")) {
            brushSettingsBuilder.includeAir(true);
        }

        Matcher matcher = Util.findInArray(args, Y_OFFSET);
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

        matcher = Util.findInArray(args, PLACEMENT);

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
}
