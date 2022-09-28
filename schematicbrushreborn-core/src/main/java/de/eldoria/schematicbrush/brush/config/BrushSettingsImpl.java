/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.PasteMutationImpl;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderImpl;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.config.util.ValueProvider;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * A brush configuration represents the settings of a single brush. A brush consists of one or more brushes represented
 * by a {@link SchematicSetImpl} object. If more than one {@link SchematicSetImpl} is present, a random {@link SchematicSetImpl}
 * will be returned via the {@link #getRandomSchematicSet()} based on the {@link SchematicSetImpl#weight()} of the
 * brushes. The brush settings contains some general brush settings, which apply to the whole brush and not only to
 * specific sub brushes.
 */
public final class BrushSettingsImpl implements BrushSettings {
    /**
     * List of all sub brushes this brush has.
     */
    private final List<SchematicSet> schematicSets;

    private final Map<PlacementModifier, Mutator<?>> placementModifier;
    /**
     * The total weight of all brushes in the {@link #schematicSets} list
     */
    private final int totalWeight;

    public BrushSettingsImpl(List<SchematicSet> schematicSets, Map<PlacementModifier, Mutator<?>> placementModifier) {
        this.schematicSets = schematicSets;
        this.placementModifier = placementModifier;

        // Count all weights, which have a weight set.
        var totalWeight = schematicSets.stream().filter(set -> set.weight() > 0).mapToInt(SchematicSet::weight).sum();
        // Count all weighted brushes
        var weighted = (int) schematicSets.stream().filter(set -> set.weight() > 0).count();
        int defaultWeight;
        // Handle case, when no brush is weighted
        if (weighted == 0) {
            defaultWeight = 1;
        } else {
            // Calculate the default weight which is the average of all brushes weights
            defaultWeight = totalWeight / weighted;
        }

        // Set the weight of all unweighted brushes
        schematicSets.stream().filter(set -> set.weight() < 0).forEach(set -> set.updateWeight(defaultWeight));

        // Calculate the total weight of all brushes
        this.totalWeight = schematicSets.stream().mapToInt(SchematicSet::weight).sum();
    }

    /**
     * Get a random brush from the {@link #schematicSets} list based on their {@link SchematicSetImpl#weight()}.
     *
     * @return a random brush
     */
    @Override
    public SchematicSet getRandomSchematicSet() {
        var random = randomInt(totalWeight);

        var count = 0;
        for (var brush : schematicSets) {
            if (count + brush.weight() > random) {
                return brush;
            }
            count += brush.weight();
        }
        return schematicSets.get(schematicSets.size() - 1);
    }

    /**
     * Counts all schematics in all brushes. No deduplication.
     *
     * @return total number of schematics in all brushes.
     */
    @Override
    public int getSchematicCount() {
        return schematicSets.stream().map(set -> set.schematics().size()).mapToInt(Integer::intValue).sum();
    }

    /**
     * Get the schematic sets of this brush
     *
     * @return list of {@link SchematicSetImpl}
     */
    @Override
    public List<SchematicSet> schematicSets() {
        return schematicSets;
    }

    /**
     * Get the total weight of all {@link BrushSettingsImpl#schematicSets()}
     *
     * @return total weight
     */
    @Override
    public int totalWeight() {
        return totalWeight;
    }

    /**
     * Get a mutator of the brush
     *
     * @param type type to request
     * @return the mutator.
     */
    @Override
    public Mutator<?> getMutator(PlacementModifier type) {
        return placementModifier.get(type);
    }

    /**
     * Mutate the {@link PasteMutationImpl} and apply the changes by the {@link BrushSettingsImpl#placementModifier}
     *
     * @param mutation mutation instance
     */
    @Override
    public void mutate(PasteMutation mutation) {
        placementModifier.values().forEach(mod -> mod.invoke(mutation));
    }

    /**
     * Converts the brush setting to a {@link BrushBuilderImpl} representing all settings applied.
     *
     * @param player            player
     * @param settingsRegistry  setting registry
     * @param schematicRegistry schematic registry
     * @return new brush builder instance
     */
    @Override
    public BrushBuilder toBuilder(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        var brushBuilder = new BrushBuilderImpl(player, settingsRegistry, schematicRegistry);
        placementModifier.forEach(brushBuilder::setPlacementModifier);
        schematicSets.stream().map(SchematicSet::toBuilder).forEach(brushBuilder::addSchematicSet);
        return brushBuilder;
    }

    @Override
    public void refreshMutator() {
        placementModifier.values().forEach(ValueProvider::refresh);
    }
}
