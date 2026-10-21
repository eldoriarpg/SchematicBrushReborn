/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config;

import de.eldoria.schematicbrush.brush.config.blockfilter.BlockFilter;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshotImpl;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilderImpl;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.flip.FlipFixed;
import de.eldoria.schematicbrush.brush.config.flip.FlipList;
import de.eldoria.schematicbrush.brush.config.flip.FlipRandom;
import de.eldoria.schematicbrush.brush.config.includeair.IncludeAir;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.offset.OffsetFixed;
import de.eldoria.schematicbrush.brush.config.offset.OffsetList;
import de.eldoria.schematicbrush.brush.config.offset.OffsetRange;
import de.eldoria.schematicbrush.brush.config.placement.Drop;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.replaceall.ReplaceAll;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.brush.config.rotation.RotationFixed;
import de.eldoria.schematicbrush.brush.config.rotation.RotationList;
import de.eldoria.schematicbrush.brush.config.rotation.RotationRandom;
import de.eldoria.schematicbrush.brush.config.selector.DirectorySelector;
import de.eldoria.schematicbrush.brush.config.selector.NameSelector;
import de.eldoria.schematicbrush.brush.config.selector.RegexSelector;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushes;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresets;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.preset.Preset;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Brushes and presets using every built-in selector and modifier, as stored by the YAML storage.
 */
final class SampleStorage {
    static final UUID PLAYER = UUID.fromString("5c1f9a4e-2f6b-4c3e-9a71-0d2b8e6f4a10");

    private SampleStorage() {
    }

    static YamlBrushes brushes() {
        var brushes = new YamlBrushes();
        brushes.playerContainer(PLAYER).add(new Brush("forest", "dense trees", snapshot(List.of(trees(), rocks()))));
        brushes.globalContainer().add(new Brush("rocks", "scattered rocks", snapshot(List.of(rocks()))));
        return brushes;
    }

    static YamlPresets presets() {
        var presets = new YamlPresets();
        presets.playerContainer(PLAYER).add(new Preset("trees", "all trees", List.of(trees(), oaks())));
        presets.globalContainer().add(new Preset("rocks", "rocks only", List.of(rocks())));
        return presets;
    }

    private static BrushBuilderSnapshot snapshot(List<SchematicSetBuilder> sets) {
        var placement = new LinkedHashMap<Nameable, Mutator<?>>();
        placement.put(PlacementModifier.PLACEMENT, new Drop());
        placement.put(PlacementModifier.INCLUDE_AIR, new IncludeAir(false));
        placement.put(PlacementModifier.REPLACE_ALL, new ReplaceAll(true));
        placement.put(PlacementModifier.OFFSET, new OffsetList(List.of(0, 1, 2)));
        placement.put(PlacementModifier.FILTER, new BlockFilter("stone,dirt"));
        placement.put(PlacementModifier.FLIP, new FlipRandom());
        placement.put(PlacementModifier.ROTATION, new RotationRandom());
        return new BrushBuilderSnapshotImpl(placement, sets);
    }

    private static SchematicSetBuilder trees() {
        var modifier = new LinkedHashMap<Nameable, Mutator<?>>();
        modifier.put(SchematicModifier.ROTATION, new RotationList(List.of(Rotation.valueOf(0), Rotation.valueOf(90))));
        modifier.put(SchematicModifier.FLIP, new FlipFixed(Flip.EAST_WEST));
        modifier.put(SchematicModifier.OFFSET, new OffsetRange(-2, 3));
        return new SchematicSetBuilderImpl(new NameSelector("tree"), modifier, 10);
    }

    private static SchematicSetBuilder oaks() {
        var modifier = new LinkedHashMap<Nameable, Mutator<?>>();
        modifier.put(SchematicModifier.FLIP, new FlipList(List.of(Flip.NONE, Flip.UP_DOWN)));
        return new SchematicSetBuilderImpl(new DirectorySelector("trees/oak", "big"), modifier, 3);
    }

    private static SchematicSetBuilder rocks() {
        var modifier = new LinkedHashMap<Nameable, Mutator<?>>();
        modifier.put(SchematicModifier.ROTATION, new RotationFixed(Rotation.valueOf(180)));
        modifier.put(SchematicModifier.OFFSET, new OffsetFixed(4));
        return new SchematicSetBuilderImpl(new RegexSelector("^rock_\\d+$"), modifier, -1);
    }
}
