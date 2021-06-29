package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.utils.ArrayUtil;
import de.eldoria.eldoutilities.utils.Parser;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.offset.IOffset;
import de.eldoria.schematicbrush.brush.config.parameter.Placement;
import de.eldoria.schematicbrush.brush.config.parameter.SchematicSelector;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.sections.Preset;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.commands.parser.ParsingUtil.parseToLegacySyntax;

public class BrushSettingsParser {
    private static final Pattern Y_OFFSET = Pattern.compile("-(?:yoff|yoffset|y):(-?[0-9]{1,3}|\\[-?[0-9]{1,3}:-?[0-9]{1,3}\\]|\\[(?:-?[0-9]{1,3},?)+?\\])$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLACEMENT = Pattern.compile("-(?:place|placement|p):([a-zA-Z]+?)$", Pattern.CASE_INSENSITIVE);

    private BrushSettingsParser() {
        throw new UnsupportedOperationException("This is a utility class.");
    }


    public static Optional<BrushSettings> parseBrush(Player player, Config config, SchematicCache schematicCache,
                                                     String[] args) {


        // Remove brush settings from arguments.
        List<String> brushes = Arrays.stream(parseToLegacySyntax(args)).filter(c -> !c.startsWith("-")).collect(Collectors.toList());


        Optional<BrushSettings.BrushSettingsBuilder> brushSettings = buildBrushes(player, brushes, config, schematicCache);

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
        BrushSettings.BrushSettingsBuilder configurationBuilder = BrushSettings.newBrushSettingsBuilder();

        MessageSender messageSender = MessageSender.getPluginMessageSender(SchematicBrushReborn.class);

        for (String settingsString : settingsStrings) {
            // Get the brush type
            Optional<SchematicSetParser.SubBrushType> optionalBrushType = SchematicSetParser.getBrushType(settingsString);

            if (!optionalBrushType.isPresent()) {
                messageSender.sendError(player, "Invalid schematic selector");
                return Optional.empty();
            }

            SchematicSetParser.SubBrushType subBrushType = optionalBrushType.get();

            // Check if its a name or regex lookup
            if (subBrushType.selectorType() == SchematicSelector.REGEX) {
                Optional<SchematicSet> brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);

                if (!brushConfig.isPresent()) {
                    return Optional.empty();
                }
                configurationBuilder.addBrush(brushConfig.get());
                continue;
            }

            // Check if its a directory lookup
            if (subBrushType.selectorType() == SchematicSelector.DIRECTORY) {
                Optional<SchematicSet> brushConfig = buildBrushConfig(player, subBrushType, settingsString, schematicCache);

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

                Optional<Preset> brushConfigs = config.getPreset(subBrushType.selectorValue());

                if (!brushConfigs.isPresent()) {
                    messageSender.sendError(player, "The preset " + subBrushType.selectorValue()
                            + " does not contain any brushes");
                    return Optional.empty();
                }

                for (String settings : brushConfigs.get().getFilter()) {
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

                    Optional<SchematicSet> brushConfig = buildBrushConfig(player, optionalBrushType.get(), settings, schematicCache);
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

        MessageSender messageSender = MessageSender.getPluginMessageSender(SchematicBrushReborn.class);

        // Check if its a name or regex lookup
        if (type.selectorType() == SchematicSelector.REGEX) {
            schematics = schematicCache.getSchematicsByName(type.selectorValue());
            schematicSetBuilder = new SchematicSet.SchematicSetBuilder(settingsString);
        }

        // Check if its a directory lookup
        if (type.selectorType() == SchematicSelector.DIRECTORY) {
            String[] split = type.selectorValue().split("#");
            String filter = split.length > 1 ? split[1] : null;
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

        SchematicSetParser.SubBrushValues subBrushValues = SchematicSetParser.getBrushValues(settingsString);

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

            if (value.startsWith("[") && value.endsWith("]")) {
                String stripped = value.substring(1, value.length() - 1);
                if (stripped.contains(":")) {
                    String[] split = stripped.split(":");
                    OptionalInt min = Parser.parseInt(split[0]);
                    OptionalInt max = Parser.parseInt(split[1]);
                    if (!(min.isPresent() && max.isPresent()) || min.getAsInt() > max.getAsInt()) {
                        MessageSender.getPluginMessageSender(SchematicBrushReborn.class)
                                .sendError(player, "Invalid offset.");
                        return Optional.empty();
                    }
                    settingsBuilder.withYOffset(IOffset.range(min.getAsInt(), max.getAsInt()));
                } else if (stripped.contains(",")) {
                    String[] stringNumbers = stripped.split(",");
                    List<Integer> numbers = new ArrayList<>();
                    for (String stringNumber : stringNumbers) {
                        OptionalInt optionalInt = Parser.parseInt(stringNumber);
                        if (!optionalInt.isPresent()) {
                            MessageSender.getPluginMessageSender(SchematicBrushReborn.class)
                                    .sendError(player, "Invalid offset.");
                            return Optional.empty();
                        }
                        numbers.add(optionalInt.getAsInt());
                    }
                    settingsBuilder.withYOffset(IOffset.list(numbers));
                }
            } else {
                OptionalInt optionOffset = Parser.parseInt(value);
                if (!optionOffset.isPresent()) {
                    MessageSender.getPluginMessageSender(SchematicBrushReborn.class)
                            .sendError(player, "Invalid offset.");
                    return Optional.empty();
                }
                settingsBuilder.withYOffset(IOffset.fixed(optionOffset.getAsInt()));
            }
        } else {
            matcher = ArrayUtil.findInArray(args, Y_OFFSET_FLAG);
            if (matcher != null) {
                MessageSender.getPluginMessageSender(SchematicBrushReborn.class)
                        .sendError(player, "Invalid offset.");
                return Optional.empty();
            }
        }

        matcher = ArrayUtil.findInArray(args, PLACEMENT);

        if (matcher != null) {
            String value = matcher.group(1);
            Optional<Placement> placement = Placement.asPlacement(value);
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
