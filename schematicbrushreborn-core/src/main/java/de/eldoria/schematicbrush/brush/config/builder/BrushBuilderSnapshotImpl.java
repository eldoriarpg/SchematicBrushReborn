/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrushBuilderSnapshotImpl implements BrushBuilderSnapshot {
    private Map<Nameable, Mutator<?>> placementModifier;
    private List<SchematicSetBuilder> schematicSets;

    public BrushBuilderSnapshotImpl() {
    }

    public BrushBuilderSnapshotImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        schematicSets = map.getValue("schematicSets");
        placementModifier = map.getMap("placementModifier", (key, v) -> Nameable.of(key));
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("placementModifier", placementModifier, (key, v) -> key.name())
                .add("schematicSets", schematicSets)
                .build();
    }

    public BrushBuilderSnapshotImpl(Map<Nameable, Mutator<?>> placementModifier, List<SchematicSetBuilder> schematicSets) {
        this.placementModifier = placementModifier;
        this.schematicSets = schematicSets;
    }

    @Override
    public Map<Nameable, Mutator<?>> placementModifier() {
        return Collections.unmodifiableMap(placementModifier);
    }

    @Override
    public List<SchematicSetBuilder> schematicSets() {
        return Collections.unmodifiableList(schematicSets);
    }

    @Override
    public BrushBuilder load(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        Map<Nameable, Mutator<?>> mutatorMap = placementModifier.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, k -> k.getValue().copy()));
        var schematicSets = this.schematicSets.stream()
                .map(SchematicSetBuilder::copy)
                .collect(Collectors.toCollection(ArrayList::new));
        return new BrushBuilderImpl(schematicSets, player, settingsRegistry, schematicRegistry, mutatorMap);
    }
}
