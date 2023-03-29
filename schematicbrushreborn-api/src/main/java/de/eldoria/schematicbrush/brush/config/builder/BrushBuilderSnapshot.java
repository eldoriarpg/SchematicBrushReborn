/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents a snapshot of a {@link BrushBuilder}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@clazz")
public interface BrushBuilderSnapshot extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    /**
     * Placement modifiers of the snapshot.
     *
     * @return placement modifiers as map
     */
    Map<Nameable, Mutator<?>> placementModifier();

    /**
     * Schematic sets of the snapshot
     *
     * @return list of schematic sets
     */
    List<SchematicSetBuilder> schematicSets();

    /**
     * Loads a brush builder snapshot.
     * <p>
     * This is required to inject required dependencies which were not serialized.
     *
     * @param player            player of the builder
     * @param settingsRegistry  settings registry
     * @param schematicRegistry schematic registry
     * @return new BrushBuilder instance.
     */
    BrushBuilder load(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry);
}
