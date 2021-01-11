package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.parameter.Placement;
import de.eldoria.schematicbrush.brush.config.parameter.SchematicSelector;
import de.eldoria.schematicbrush.commands.util.MessageSender;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.ArrayUtil;
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

import static de.eldoria.schematicbrush.commands.parser.ParsingUtil.parseToLegacySyntax;

@UtilityClass
public class BrushSettingsParser {
    private static final Pattern Y_OFFSET = Pattern.compile("-(?:(?:yoff)|(?:yoffset)|(?:y)):(-?[0-9]{1,3})$", Pattern.CASE_INSENSITIVE);
    private final Pattern PLACEMENT = Pattern.compile("-(?:(?:place)|(?:placement)|(?:p)):([a-zA-Z]+?)$", Pattern.CASE_INSENSITIVE);


    public Optional<BrushSettings> parseBrush(Player player, Plugin plugin, SchematicCache schematicCache,
                                              String[] args) {


        // Remove brush settings from arguments.
        List<String> brushes = Arrays.stream(parseToLegacySyntax(args)).filter(c -> !c.startsWith("-")).collect(Collectors.toList());


        Optional<BrushSettings.BrushSettingsBuilder> brushSettings = buildBrushes(player, brushes, plugin, schematicCache);

        // Check if somethin went wrong while creating the brush.
        if (!brushSettings.isPresent()) return Optional.empty();

        return buildBrushSettings(player, brushSettings.get(), args);
    }

    /**
     * Build brushes from one or more brush argument strings
     *
     * @param player          executor of the brush
     * @param settingsStrings one or more brushes
     * @param plugin          plugin instance
     * @param schematicCache  schematic cache instance
     *
     * @return A optional, which returns a unconfigured {@link BrushSettings.BrushSettingsBuilder} with brushes already
     * set or empty if a brush string could not be parsed
     */
    public Optional<BrushSettings.BrushSettingsBuilder> buildBrushes(Player player, List<String> settingsStrings, Plugin plugin,
                                                                     SchematicCache schematicCache) {
        BrushSettings.BrushSettingsBuilder configurationBuilder = BrushSettings.newBrushSettingsBuilder();

        for (String settingsString : settingsStrings) {
            // Get the brush type
            Optional<SchematicSetParser.SubBrushType> optionalBrushType = SchematicSetParser.getBrushType(settingsString);

            if (!optionalBrushType.isPresent()) {
                MessageSender.sendError(player, "Invalid schematic selector");
                return Optional.empty();
            }

            SchematicSetParser.SubBrushType subBrushType = optionalBrushType.get();

            // Check if its a name or regex lookup
            if (subBrushType.getSelectorType() == SchematicSelector.REGEX) {
                Optional<SchematicSet> brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);

                if (!brushConfig.isPresent()) {
                    return Optional.empty();
                }
                configurationBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a directory lookup
            if (subBrushType.getSelectorType() == SchematicSelector.DIRECTORY) {
                Optional<SchematicSet> brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);

                if (!brushConfig.isPresent()) {
                    MessageSender.sendError(player, settingsString + " is invalid");
                    return Optional.empty();
                }
                configurationBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a preset
            if (subBrushType.getSelectorType() == SchematicSelector.PRESET) {
                // check if brush exists
                if (!plugin.getConfig().contains("presets." + subBrushType.getSelectorValue())) {
                    MessageSender.sendError(player, "This brush preset"
                            + subBrushType.getSelectorValue() + " does not exist.");
                    return Optional.empty();
                }

                // Get list of brush arguments.

                Optional<List<String>> brushConfigs = getBrushesFromConfig(subBrushType.getSelectorValue(), plugin);

                if (!brushConfigs.isPresent()) {
                    MessageSender.sendError(player, "The preset " + subBrushType.getSelectorValue()
                            + " does not contain any brushes");
                    return Optional.empty();
                }

                for (String settings : brushConfigs.get()) {
                    optionalBrushType = SchematicSetParser.getBrushType(settings);

                    if (!optionalBrushType.isPresent()) {
                        MessageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    // Block if a preset is used in a preset to avoid loop calls.
                    if (optionalBrushType.get().getSelectorType() == SchematicSelector.PRESET) {
                        MessageSender.sendError(player, "Presets are not allowed in presets.");
                        return Optional.empty();
                    }

                    Optional<SchematicSet> config = buildBrushConfig(player, optionalBrushType.get(), settings, schematicCache);
                    if (!config.isPresent()) {
                        MessageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    configurationBuilder.addBrush(config.get());
                }
            }
        }
        return Optional.of(configurationBuilder);
    }

    private Optional<SchematicSet> buildBrushConfig(Player player, SchematicSetParser.SubBrushType type,
                                                    String settingsString, SchematicCache schematicCache) {
        SchematicSet.SchematicSetBuilder schematicSetBuilder = null;

        Set<Schematic> schematics = Collections.emptySet();

        // Check if its a name or regex lookup
        if (type.getSelectorType() == SchematicSelector.REGEX) {
            schematics = schematicCache.getSchematicsByName(type.getSelectorValue());
            schematicSetBuilder = new SchematicSet.SchematicSetBuilder(settingsString);
        }

        // Check if its a directory lookup
        if (type.getSelectorType() == SchematicSelector.DIRECTORY) {
            schematics = schematicCache.getSchematicsByDirectory(type.getSelectorValue());
            schematicSetBuilder = new SchematicSet.SchematicSetBuilder(settingsString);
        }


        // If no builder was initialized the expession is invalid.
        if (schematicSetBuilder == null) {
            MessageSender.sendError(player, "Invalid name type.");
            return Optional.empty();
        }

        if (schematics.isEmpty()) {
            MessageSender.sendError(player, "No schematics were found for " + settingsString);
            return Optional.empty();
        }

        schematicSetBuilder.withSchematics(schematics);

        SchematicSetParser.SubBrushValues subBrushValues = SchematicSetParser.getBrushValues(settingsString);

        // Read rotation
        if (subBrushValues.getRotation() != null) {
            schematicSetBuilder.withRotation(subBrushValues.getRotation());
        } else if (settingsString.contains("@")) {
            MessageSender.sendError(player, "Invalid rotation!");
            return Optional.empty();
        }

        // Read flip
        if (subBrushValues.getFlip() != null) {
            schematicSetBuilder.withFlip(subBrushValues.getFlip());
        } else if (settingsString.contains("!")) {
            MessageSender.sendError(player, "Invalid flip!");
            return Optional.empty();
        }

        // Read weight
        if (subBrushValues.getWeight() != null) {
            schematicSetBuilder.withWeight(subBrushValues.getWeight());
        } else if (settingsString.contains(":")) {
            MessageSender.sendError(player, "Invalid weight!");
            return Optional.empty();
        }


        return Optional.ofNullable(schematicSetBuilder.build());
    }

    /**
     * Build a new Brush from a {@link BrushSettings.BrushSettingsBuilder}
     *
     * @param player          executor of the brush
     * @param settingsBuilder Unconfigures builder for brush settings
     * @param args            arguments of the brush
     *
     * @return optional configured brush settings object or empty if something could not be parsed
     */
    private Optional<BrushSettings> buildBrushSettings(Player player, BrushSettings.BrushSettingsBuilder settingsBuilder,
                                                       String[] args) {
        List<String> strings = Arrays.asList(args);

        if (ArrayUtil.arrayContains(args, "-includeair", "-incair", "-a")) {
            settingsBuilder.includeAir(true);
        }

        if (ArrayUtil.arrayContains(args, "-replaceall", "-repla", "-r")) {
            settingsBuilder.replaceAll(true);
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
            settingsBuilder.withYOffset(offset);
        }

        matcher = ArrayUtil.findInArray(args, PLACEMENT);

        if (matcher != null) {
            String value = matcher.group(1);
            Placement placement = Placement.asPlacement(value);
            settingsBuilder.withPlacementType(placement);
        }

        return Optional.of(settingsBuilder.build());
    }

    private Optional<List<String>> getBrushesFromConfig(String presetName, Plugin plugin) {
        String path = "presets." + presetName + ".filter";
        if (plugin.getConfig().contains(path)) {
            return Optional.of(plugin.getConfig().getStringList(path));
        }
        return Optional.empty();
    }

}
