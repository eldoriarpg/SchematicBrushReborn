/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.schematics.SchematicSelection;
import de.eldoria.schematicbrush.brush.config.util.Randomable;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Setting of a {@link de.eldoria.schematicbrush.brush.SchematicBrush}
 */
public interface BrushSettings extends Randomable {
    /**
     * Get the next schematic and the set containing it from the {@link #schematicSets} list based on their {@link SchematicSet#weight()}.
     *
     * @return a random brush
     */
    Optional<Pair<SchematicSet, Schematic>> nextSchematic(SchematicBrush brush);

    /**
     * Counts all schematics in all brushes. No deduplication.
     *
     * @return total number of schematics in all brushes.
     */
    int getSchematicCount();

    /**
     * Get the schematic sets of this brush
     *
     * @return list of {@link SchematicSet}
     */
    List<SchematicSet> schematicSets();

    /**
     * Get the total weight of all {@link BrushSettings#schematicSets()}
     *
     * @return total weight
     */
    int totalWeight();

    /**
     * Get a mutator of the brush
     *
     * @param type type to request
     * @return the mutator.
     */
    Mutator<?> getMutator(PlacementModifier type);

    /**
     * Mutate the {@link PasteMutation}
     *
     * @param mutation mutation instance
     */
    void mutate(PasteMutation mutation);

    /**
     * Converts the brush setting to a {@link BrushBuilder} representing all settings applied.
     *
     * @param player            player
     * @param settingsRegistry  setting registry
     * @param schematicRegistry schematic registry
     * @return new brush builder instance
     */
    BrushBuilder toBuilder(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry);

    /**
     * Refresehs the underlying mutators.
     */
    void refreshMutator();

    /**
     * Get the current schematic selection mode
     * @return schematic selection mode
     */
    SchematicSelection schematicSelection();

    void schematicSelection(SchematicSelection schematicSelection);
}
