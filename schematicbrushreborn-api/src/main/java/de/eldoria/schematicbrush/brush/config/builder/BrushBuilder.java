/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builder to build brushes.
 */
public interface BrushBuilder {
    /**
     * Get the schematic set builder for the set with the id
     *
     * @param id id
     * @return set if the id exists
     */
    Optional<SchematicSetBuilder> getSchematicSet(int id);

    /**
     * Remove the set with the id
     *
     * @param id id
     * @return true if the set with the id was removed
     */
    boolean removeSchematicSet(int id);

    /**
     * Create a new schematic set
     *
     * @return id of the created schematic set
     */
    int createSchematicSet();

    /**
     * Sets the placement modifier
     *
     * @param type     type
     * @param provider provider
     * @param <T>      Type of the placement modifier
     */
    <T extends PlacementModifier> void setPlacementModifier(T type, Mutator<?> provider);

    /**
     * Removes the placement modifier
     *
     * @param type     type
     * @param <T>      Type of the placement modifier
     */
    <T extends PlacementModifier> void removePlacementModifier(T type);

    /**
     * Build the schematic brush
     *
     * @param plugin plugin
     * @param owner  brush owner
     * @return new schematic brush instance
     */
    SchematicBrush build(Plugin plugin, Player owner);

    /**
     * Add a new schematic set builder
     *
     * @param schematicSetBuilder builder to add
     */
    void addSchematicSet(SchematicSetBuilder schematicSetBuilder);

    /**
     * Get the registered schematic set builder
     *
     * @return unmodifiable list of schematic sets
     */
    List<SchematicSetBuilder> schematicSets();

    /**
     * Get the current placement modifier
     *
     * @return unmodifiable map of the placement modifier
     */
    Map<? extends PlacementModifier, Mutator<?>> placementModifier();

    /**
     * Reset all modifier to default and clear schematic sets.
     */
    void clear();

    /**
     * Get the schematic count of the brush
     *
     * @return schematic count
     */
    int getSchematicCount();

    /**
     * Reload all schematics in the brush
     */
    void refresh();

    /**
     * Get a immutable snapshot of the brush builder.
     *
     * @return snapshot
     */
    BrushBuilderSnapshot snapshot();
}
