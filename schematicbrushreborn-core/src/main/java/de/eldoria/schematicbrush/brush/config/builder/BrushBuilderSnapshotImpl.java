/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrushBuilderSnapshotImpl implements BrushBuilderSnapshot {
    private final Map<Nameable, Mutator<?>> placementModifier;
    private final List<SchematicSetBuilder> schematicSets;

    @JsonCreator
    public BrushBuilderSnapshotImpl(@JsonProperty("placementModifier") Map<Nameable, Mutator<?>> placementModifier,
                                    @JsonProperty("schematicSets") List<SchematicSetBuilder> schematicSets) {
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
                .collect(Collectors.toMap(Map.Entry::getKey, key -> key.getValue().copy()));
        var schematicSets = this.schematicSets.stream()
                .map(SchematicSetBuilder::copy)
                .collect(Collectors.toCollection(ArrayList::new));
        var brushBuilder = new BrushBuilderImpl(schematicSets, player, settingsRegistry, schematicRegistry, mutatorMap);
        brushBuilder.refresh();
        return brushBuilder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrushBuilderSnapshot snapshot)) return false;

        if (!placementModifier.equals(snapshot.placementModifier())) return false;
        return schematicSets.equals(snapshot.schematicSets());
    }

    @Override
    public int hashCode() {
        var result = placementModifier.hashCode();
        result = 31 * result + schematicSets.hashCode();
        return result;
    }
}
