package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.schematicbrush.commands.util.MessageSender;
import de.eldoria.schematicbrush.util.ArrayUtil;
import de.eldoria.schematicbrush.brush.config.parameter.BrushSelector;
import de.eldoria.schematicbrush.brush.config.BrushConfiguration;
import de.eldoria.schematicbrush.brush.config.SubBrush;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.brush.config.parameter.Placement;
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

    public Optional<BrushConfiguration> parseBrush(Player player, Plugin plugin, SchematicCache schematicCache,
                                                   String[] args) {
        // Remove brush settings from arguments.
        List<String> brushes = Arrays.stream(args).filter(c -> !c.startsWith("-")).collect(Collectors.toList());

        Optional<BrushConfiguration.BrushConfigurationBuilder> brushSettings = buildBrushes(player, brushes, plugin, schematicCache);

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
     * @return A optional, which returns a unconfigured {@link BrushConfiguration.BrushConfigurationBuilder} with brushes already set
     * or empty if a brush string could not be parsed
     */
    private Optional<BrushConfiguration.BrushConfigurationBuilder> buildBrushes(Player player, List<String> settingsStrings, Plugin plugin,
                                                                                SchematicCache schematicCache) {

        BrushConfiguration.BrushConfigurationBuilder brushConfigurationBuilder = BrushConfiguration.newBrushSettingsBuilder();


        for (String settingsString : settingsStrings) {
            // Get the brush type
            Optional<BrushArgumentParser.SubBrushType> optionalBrushType = BrushArgumentParser.getBrushType(settingsString);
            if (optionalBrushType.isEmpty()) {
                MessageSender.sendError(player, "Invalid schematic selector");
                return Optional.empty();
            }

            BrushArgumentParser.SubBrushType subBrushType = optionalBrushType.get();

            // Check if its a name or regex lookup
            if (subBrushType.getSelectorType() == BrushSelector.REGEX) {
                Optional<SubBrush> brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);
                if (brushConfig.isEmpty()) {
                    return Optional.empty();
                }
                brushConfigurationBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a directory lookup
            if (subBrushType.getSelectorType() == BrushSelector.DIRECTORY) {
                Optional<SubBrush> brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);
                if (brushConfig.isEmpty()) {
                    MessageSender.sendError(player, settingsString + " is invalid");
                    return Optional.empty();
                }
                brushConfigurationBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a preset
            if (subBrushType.getSelectorType() == BrushSelector.PRESET) {
                if (!plugin.getConfig().contains("presets." + subBrushType.getSelectorValue())) {
                    MessageSender.sendError(player, "This brush preset"
                            + subBrushType.getSelectorValue() + " does not exist.");
                    return Optional.empty();
                }

                // Get list of brush arguments.
                List<String> brushConfigs = plugin.getConfig().getStringList("presets." + subBrushType.getSelectorValue());

                if (brushConfigs.isEmpty()) {
                    MessageSender.sendError(player, "This preset " + subBrushType.getSelectorValue()
                            + " does not contain any brushes");
                    return Optional.empty();
                }

                for (String settings : brushConfigs) {
                    optionalBrushType = BrushArgumentParser.getBrushType(settings);

                    if (optionalBrushType.isEmpty()) {
                        MessageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    // Block if a preset is used in a preset to avoid loop calls.
                    if (optionalBrushType.get().getSelectorType() == BrushSelector.PRESET) {
                        MessageSender.sendError(player, "Presets are not allowed in presets.");
                        return Optional.empty();
                    }

                    Optional<SubBrush> config = buildBrushConfig(player, subBrushType, settings, schematicCache);
                    if (config.isEmpty()) {
                        MessageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    brushConfigurationBuilder.addBrush(config.get());
                }
            }
        }
        return Optional.of(brushConfigurationBuilder);
    }

    private Optional<SubBrush> buildBrushConfig(Player player, BrushArgumentParser.SubBrushType type,
                                                String settingsString, SchematicCache schematicCache) {
        SubBrush.SubBrushBuilder subBrushBuilder = null;

        List<Schematic> schematics = Collections.emptyList();

        // Check if its a name or regex lookup
        if (type.getSelectorType() == BrushSelector.REGEX) {
            schematics = schematicCache.getSchematicsByName(type.getSelectorValue());
            subBrushBuilder = new SubBrush.SubBrushBuilder(settingsString);
        }

        // Check if its a directory lookup
        if (type.getSelectorType() == BrushSelector.DIRECTORY) {
            schematics = schematicCache.getSchematicsByDirectory(type.getSelectorValue());
            subBrushBuilder = new SubBrush.SubBrushBuilder(settingsString);
        }


        // If no builder was initialized the expession is invalid.
        if (subBrushBuilder == null) {
            MessageSender.sendError(player, "Invalid name type.");
            return Optional.empty();
        }

        if (schematics.isEmpty()) {
            MessageSender.sendError(player, "No schematics were found for " + settingsString);
            return Optional.empty();
        }

        subBrushBuilder.withSchematics(schematics);

        BrushArgumentParser.SubBrushValues subBrushValues = BrushArgumentParser.getBrushValues(settingsString);

        // Read rotation
        if (subBrushValues.getRotation() != null) {
            subBrushBuilder.withRotation(subBrushValues.getRotation());
        } else if (settingsString.contains("@")) {
            MessageSender.sendError(player, "Invalid rotation!");
            return Optional.empty();
        }

        // Read flip
        if (subBrushValues.getFlip() != null) {
            subBrushBuilder.withFlip(subBrushValues.getFlip());
        } else if (settingsString.contains("!")) {
            MessageSender.sendError(player, "Invalid flip!");
            return Optional.empty();
        }

        // Read weight
        if (subBrushValues.getWeight() != null) {
            subBrushBuilder.withWeight(subBrushValues.getWeight());
        } else if (settingsString.contains(":")) {
            MessageSender.sendError(player, "Invalid weight!");
            return Optional.empty();
        }


        return Optional.ofNullable(subBrushBuilder.build());
    }

    /**
     * Build a new Brush from a {@link BrushConfiguration.BrushConfigurationBuilder}
     *
     * @param player                                 executor of the brush
     * @param brushSettingsBrushConfigurationBuilder Unconfigures builder for brush settings
     * @param args                                   arguments of the brush
     * @return optional configured brush settings object or empty if something could not be parsed
     */
    private Optional<BrushConfiguration> buildBrushSettings(Player player, BrushConfiguration.BrushConfigurationBuilder brushSettingsBrushConfigurationBuilder,
                                                            String[] args) {
        List<String> strings = Arrays.asList(args);

        if (ArrayUtil.arrayContains(args, "-includeair", "-incair", "-a")) {
            brushSettingsBrushConfigurationBuilder.includeAir(true);
        }

        if (ArrayUtil.arrayContains(args, "-replaceall", "-repla", "-r")) {
            brushSettingsBrushConfigurationBuilder.includeAir(true);
        }

        Matcher matcher = ArrayUtil.findInArray(args, Y_OFFSET);
        if (matcher != null) {
            String value = matcher.group(1);
            int offset;
            try {
                offset = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                MessageSender.sendError(player, "Invalid offset.");
                return Optional.empty();
            }
            brushSettingsBrushConfigurationBuilder.withYOffset(offset);
        }

        matcher = ArrayUtil.findInArray(args, PLACEMENT);

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
            brushSettingsBrushConfigurationBuilder.withYOffset(offset);
        }

        return Optional.of(brushSettingsBrushConfigurationBuilder.build());
    }
}
