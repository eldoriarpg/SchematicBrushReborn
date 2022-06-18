/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface BrushBuilderSnapshot extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    Map<Nameable, Mutator<?>> placementModifier();

    List<SchematicSetBuilder> schematicSets();

    /**
     * Loads a brush builder snapshot.
     *
     * This is required to inject required dependencies which were not serialized.
     *
     * @param player player of the builder
     * @param settingsRegistry settings registry
     * @param schematicRegistry schematic registry
     * @return new BrushBuilder instance.
     */
    BrushBuilder load(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry);
}
