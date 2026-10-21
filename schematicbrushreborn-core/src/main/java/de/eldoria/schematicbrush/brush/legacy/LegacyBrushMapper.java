/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderImpl;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilderImpl;
import de.eldoria.schematicbrush.brush.config.flip.AFlip;
import de.eldoria.schematicbrush.brush.config.includeair.IncludeAir;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.offset.AOffset;
import de.eldoria.schematicbrush.brush.config.placement.APlacement;
import de.eldoria.schematicbrush.brush.config.placement.Bottom;
import de.eldoria.schematicbrush.brush.config.placement.Drop;
import de.eldoria.schematicbrush.brush.config.placement.Middle;
import de.eldoria.schematicbrush.brush.config.placement.Original;
import de.eldoria.schematicbrush.brush.config.placement.Raise;
import de.eldoria.schematicbrush.brush.config.placement.Top;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.replaceall.ReplaceAll;
import de.eldoria.schematicbrush.brush.config.rotation.ARotation;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.brush.config.schematics.RandomSelection;
import de.eldoria.schematicbrush.brush.config.selector.DirectorySelector;
import de.eldoria.schematicbrush.brush.config.selector.NameSelector;
import de.eldoria.schematicbrush.brush.config.selector.RegexSelector;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Definition;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.FlipSpec;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.OffsetSpec;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Options;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Placement;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.RotationSpec;
import de.eldoria.schematicbrush.brush.provider.FilterProvider;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns a {@link LegacyBrush} into a brush builder.
 * <p>
 * Each definition becomes a schematic set. A plain name or {@code *} pattern selects by name, a name with {@code ?} or a
 * {@code ^} regex selects by regular expression, and a path selects within a directory ({@code dir/**} includes its sub
 * directories). Without {@code -place} the brush is placed at the middle, as the original did.
 */
public final class LegacyBrushMapper {
    private static final String REGEX_SPECIALS = "\\.[]{}()<>+-=!|$^";

    private final Plugin plugin;
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematics;
    private final String pathSeparator;

    public LegacyBrushMapper(Plugin plugin, BrushSettingsRegistry registry, SchematicRegistry schematics, String pathSeparator) {
        this.plugin = plugin;
        this.registry = registry;
        this.schematics = schematics;
        this.pathSeparator = pathSeparator;
    }

    /**
     * Builds the brush.
     *
     * @param player     owner of the brush
     * @param brush      parsed brush
     * @param presetSets sets of the presets the expression names
     * @return builder holding the brush
     * @throws ParseException if a definition matches no schematic or the filter is not a valid mask
     */
    public BrushBuilder map(Player player, LegacyBrush brush, List<SchematicSetBuilder> presetSets) throws ParseException {
        var builder = new BrushBuilderImpl(player, new RandomSelection(), registry, schematics);
        for (var definition : brush.definitions()) {
            var set = set(definition);
            builder.addSchematicSet(set);
            if (set.schematicCount() == 0) throw new ParseException("commands.brush.parse.error.noMatch", definition.selector());
        }
        presetSets.forEach(set -> builder.addSchematicSet(set.copy()));
        options(player, builder, brush.options());
        return builder;
    }

    /**
     * The selector a definition stands for.
     *
     * @param definition definition
     * @return selector
     */
    public Selector selector(Definition definition) {
        var text = definition.selector().text();
        if (definition.regex()) return new RegexSelector("^" + text);
        var slash = text.lastIndexOf('/');
        if (slash < 0) return new NameSelector(nameTerm(text));
        var directory = text.substring(0, slash).replace("/", pathSeparator);
        var name = text.substring(slash + 1);
        if ("**".equals(name)) return new DirectorySelector(directory + "*", null);
        return new DirectorySelector(directory, name.isEmpty() ? null : nameTerm(name));
    }

    private SchematicSetBuilder set(Definition definition) {
        var set = new SchematicSetBuilderImpl(selector(definition));
        if (definition.rotation() != null) set.setModifier(SchematicModifier.ROTATION, rotation(definition.rotation()));
        if (definition.flip() != null) set.setModifier(SchematicModifier.FLIP, flip(definition.flip()));
        if (definition.offset() != null) set.setModifier(SchematicModifier.OFFSET, offset(definition.offset()));
        if (definition.weight() != null) set.withWeight(definition.weight());
        return set;
    }

    private void options(Player player, BrushBuilder builder, Options options) throws ParseException {
        var placement = options.placement() == null ? Placement.MIDDLE : options.placement();
        builder.setPlacementModifier(PlacementModifier.PLACEMENT, placement(placement));
        builder.setPlacementModifier(PlacementModifier.INCLUDE_AIR, new IncludeAir(options.includeAir()));
        builder.setPlacementModifier(PlacementModifier.REPLACE_ALL, new ReplaceAll(options.replaceAll()));
        if (options.offset() != null) builder.setPlacementModifier(PlacementModifier.OFFSET, offset(options.offset()));
        if (options.rotation() != null) builder.setPlacementModifier(PlacementModifier.ROTATION, rotation(options.rotation()));
        if (options.flip() != null) builder.setPlacementModifier(PlacementModifier.FLIP, flip(options.flip()));
        if (options.filter() != null) {
            try {
                var mask = Arguments.create(plugin, player, new String[]{options.filter().text()});
                builder.setPlacementModifier(PlacementModifier.FILTER, FilterProvider.BLOCK_FILTER.parse(mask));
            } catch (CommandException e) {
                throw new ParseException(e.getMessage(), options.filter());
            }
        }
    }

    private static Mutator<?> rotation(RotationSpec spec) {
        return switch (spec) {
            case RotationSpec.Random random -> ARotation.random();
            case RotationSpec.Degrees degrees when degrees.degrees().size() == 1 -> ARotation.fixed(rotation(degrees.degrees().get(0)));
            case RotationSpec.Degrees degrees -> ARotation.list(degrees.degrees().stream().map(LegacyBrushMapper::rotation).toList());
        };
    }

    private static Rotation rotation(int degrees) {
        return switch (degrees) {
            case 90 -> Rotation.ROT_RIGHT;
            case 180 -> Rotation.ROT_HALF;
            case 270 -> Rotation.ROT_LEFT;
            default -> Rotation.ROT_ZERO;
        };
    }

    private static Mutator<?> flip(FlipSpec spec) {
        return switch (spec) {
            case FlipSpec.Random random -> AFlip.random();
            case FlipSpec.Axes axes when axes.axes().size() == 1 -> AFlip.fixed(axes.axes().get(0));
            case FlipSpec.Axes axes -> AFlip.list(new ArrayList<>(axes.axes()));
        };
    }

    private static Mutator<?> offset(OffsetSpec spec) {
        return switch (spec) {
            case OffsetSpec.Fixed fixed -> AOffset.fixed(fixed.value());
            case OffsetSpec.Range range -> AOffset.range(range.min(), range.max());
            case OffsetSpec.Values values -> AOffset.list(new ArrayList<>(values.values()));
        };
    }

    private static APlacement placement(Placement placement) {
        return switch (placement) {
            case MIDDLE -> new Middle();
            case BOTTOM -> new Bottom();
            case DROP -> new Drop();
            case RAISE -> new Raise();
            case TOP -> new Top();
            case ORIGINAL -> new Original();
        };
    }

    /**
     * Turns a name with {@code ?} wildcards into a regular expression, since name matching only knows {@code *}.
     */
    private static String nameTerm(String name) {
        if (name.indexOf('?') < 0) return name;
        var regex = new StringBuilder("^");
        for (var character : name.toCharArray()) {
            switch (character) {
                case '*' -> regex.append(".*");
                case '?' -> regex.append('.');
                default -> {
                    if (REGEX_SPECIALS.indexOf(character) >= 0) regex.append('\\');
                    regex.append(character);
                }
            }
        }
        return regex.toString();
    }
}
