package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.utils.ArrayUtil;
import de.eldoria.eldoutilities.utils.Parser;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.offset.AOffset;
import de.eldoria.schematicbrush.brush.config.parameter.Placement;
import de.eldoria.schematicbrush.brush.config.parameter.SchematicSelector;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.sections.Preset;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.commands.parser.ParsingUtil.parseToLegacySyntax;

public class BrushSettingsParser {
    private static final Pattern Y_OFFSET = Pattern.compile("-(?:yoff|yoffset|y):(-?[0-9]{1,3}|\\[-?[0-9]{1,3}:-?[0-9]{1,3}\\]|\\[(?:-?[0-9]{1,3},?)*?[^,]\\])$", Pattern.CASE_INSENSITIVE);
    private static final Pattern Y_OFFSET_FLAG = Pattern.compile("-(?:yoff|yoffset|y).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLACEMENT = Pattern.compile("-(?:place|placement|p):([a-zA-Z]+?)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLACEMENT_FLAG = Pattern.compile("-(?:place|placement|p).*", Pattern.CASE_INSENSITIVE);

    private BrushSettingsParser() {
        throw new UnsupportedOperationException("This is a utility class.");
    }


    public static Optional<BrushSettings> parseBrush(Player player, Config config, SchematicCache schematicCache,
                                                     String[] args) {


        // Remove brush settings from arguments.
        var brushes = Arrays.stream(parseToLegacySyntax(args)).filter(c -> !c.startsWith("-")).collect(Collectors.toList());


        var brushSettings = buildBrushes(player, brushes, config, schematicCache);

        // Check if somethin went wrong while creating the brush.
        if (!brushSettings.isPresent()) return Optional.empty();

        return buildBrushSettings(player, brushSettings.get(), args);
    }

    /**
     * Build brushes from one or more brush argument strings
     *
     * @param player          executor of the brush
     * @param settingsStrings one or more brushes
     * @param config          plugin config
     * @param schematicCache  schematic cache instance
     * @return A optional, which returns a unconfigured {@link BrushSettings.BrushSettingsBuilder} with brushes already
     * set or empty if a brush string could not be parsed
     */
    public static Optional<BrushSettings.BrushSettingsBuilder> buildBrushes(Player player, List<String> settingsStrings, Config config,
                                                                            SchematicCache schematicCache) {
        var configurationBuilder = BrushSettings.newBrushSettingsBuilder();

        var messageSender = MessageSender.getPluginMessageSender(SchematicBrushReborn.class);

        for (var settingsString : settingsStrings) {
            // Get the brush type
            var optionalBrushType = SchematicSetParser.getBrushType(settingsString);

            if (!optionalBrushType.isPresent()) {
                messageSender.sendError(player, "Invalid schematic selector");
                return Optional.empty();
            }

            var subBrushType = optionalBrushType.get();

            // Check if its a name or regex lookup
            if (subBrushType.selectorType() == SchematicSelector.REGEX) {
                var brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);

                if (!brushConfig.isPresent()) {
                    return Optional.empty();
                }
                configurationBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a directory lookup
            if (subBrushType.selectorType() == SchematicSelector.DIRECTORY) {
                var brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);

                if (!brushConfig.isPresent()) {
                    messageSender.sendError(player, settingsString + " is invalid");
                    return Optional.empty();
                }
                configurationBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a preset
            if (subBrushType.selectorType() == SchematicSelector.PRESET) {
                // check if brush exists
                if (!config.presetExists(subBrushType.selectorValue())) {
                    messageSender.sendError(player, "This brush preset "
                                                    + subBrushType.selectorValue() + " does not exist.");
                    return Optional.empty();
                }

                // Get list of brush arguments.

                var brushConfigs = config.getPreset(subBrushType.selectorValue());

                if (!brushConfigs.isPresent()) {
                    messageSender.sendError(player, "The preset " + subBrushType.selectorValue()
                                                    + " does not contain any brushes");
                    return Optional.empty();
                }

                for (var settings : brushConfigs.get().getFilter()) {
                    optionalBrushType = SchematicSetParser.getBrushType(settings);

                    if (!optionalBrushType.isPresent()) {
                        messageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    // Block if a preset is used in a preset to avoid loop calls.
                    if (optionalBrushType.get().selectorType() == SchematicSelector.PRESET) {
                        messageSender.sendError(player, "Presets are not allowed in presets.");
                        return Optional.empty();
                    }

                    var brushConfig = buildBrushConfig(player, optionalBrushType.get(), settings, schematicCache);
                    if (!brushConfig.isPresent()) {
                        messageSender.sendError(player, settings + " is invalid");
                        return Optional.empty();
                    }

                    configurationBuilder.addBrush(brushConfig.get());
                }
            }
        }
        return Optional.of(configurationBuilder);
    }

    private static Optional<SchematicSet> buildBrushConfig(Player player, SchematicSetParser.SubBrushType type,
                                                           String settingsString, SchematicCache schematicCache) {
        SchematicSet.SchematicSetBuilder schematicSetBuilder = null;

        Set<Schematic> schematics = Collections.emptySet();

        var messageSender = MessageSender.getPluginMessageSender(SchematicBrushReborn.class);

        // Check if its a name or regex lookup
        if (type.selectorType() == SchematicSelector.REGEX) {
            schematics = schematicCache.getSchematicsByName(type.selectorValue());
            schematicSetBuilder = new SchematicSet.SchematicSetBuilder(settingsString);
        }

        // Check if its a directory lookup
        if (type.selectorType() == SchematicSelector.DIRECTORY) {
            var split = type.selectorValue().split("#");
            var filter = split.length > 1 ? split[1] : null;
            schematics = schematicCache.getSchematicsByDirectory(split[0], filter);
            schematicSetBuilder = new SchematicSet.SchematicSetBuilder(settingsString);
        }

        // If no builder was initialized the expession is invalid.
        if (schematicSetBuilder == null) {
            messageSender.sendError(player, "Invalid name type.");
            return Optional.empty();
        }

        if (schematics.isEmpty()) {
            messageSender.sendError(player, "No schematics were found for " + settingsString);
            return Optional.empty();
        }

        schematicSetBuilder.withSchematics(schematics);

        var subBrushValues = SchematicSetParser.getBrushValues(settingsString);

        // Read rotation
        if (subBrushValues.rotation() != null) {
            schematicSetBuilder.withRotation(subBrushValues.rotation());
        } else if (settingsString.contains("@")) {
            messageSender.sendError(player, "Invalid rotation!");
            return Optional.empty();
        }

        // Read flip
        if (subBrushValues.flip() != null) {
            schematicSetBuilder.withFlip(subBrushValues.flip());
        } else if (settingsString.contains("!")) {
            messageSender.sendError(player, "Invalid flip!");
            return Optional.empty();
        }

        // Read weight
        if (subBrushValues.weight() != null) {
            schematicSetBuilder.withWeight(subBrushValues.weight());
        } else if (settingsString.contains(":")) {
            messageSender.sendError(player, "Invalid weight!");
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
     * @return optional configured brush settings object or empty if something could not be parsed
     */
    private static Optional<BrushSettings> buildBrushSettings(Player player, BrushSettings.BrushSettingsBuilder settingsBuilder,
                                                              String[] args) {
        if (ArrayUtil.arrayContains(args, "-includeair", "-incair", "-a")) {
            settingsBuilder.includeAir(true);
        }

        if (ArrayUtil.arrayContains(args, "-replaceall", "-repla", "-r")) {
            settingsBuilder.replaceAll(true);
        }

        var matcher = ArrayUtil.findInArray(args, Y_OFFSET);
        if (matcher != null) {
            var value = matcher.group(1);

            var parseResult = ParsingUtil.parseValue(value, Parser::parseInt);
            switch (parseResult.type()) {
                case RANDOM:
                case NONE:
                    MessageSender.getPluginMessageSender(SchematicBrushReborn.class)
                            .sendError(player, "Invalid offset.");
                    return Optional.empty();
                case LIST:
                    settingsBuilder.withYOffset(AOffset.list(parseResult.results()));
                case RANGE:
                    var range = parseResult.range();
                    if (range.first > range.second) {
                        MessageSender.getPluginMessageSender(SchematicBrushReborn.class)
                                .sendError(player, "Invalid offset.");
                        return Optional.empty();
                    }
                    settingsBuilder.withYOffset(AOffset.range(range.first, range.second));
                    break;
                case FIXED:
                    settingsBuilder.withYOffset(AOffset.fixed(parseResult.result()));
                    break;
            }
        }

        matcher = ArrayUtil.findInArray(args, PLACEMENT);

        if (matcher != null) {
            var value = matcher.group(1);
            var placement = Placement.asPlacement(value);
            if (!placement.isPresent()) {
                MessageSender.getPluginMessageSender(SchematicBrushReborn.class)
                        .sendError(player, "Invalid placement.");
                return Optional.empty();
            }
            settingsBuilder.withPlacementType(placement.get());
        }
        return Optional.of(settingsBuilder.build());
    }
}
