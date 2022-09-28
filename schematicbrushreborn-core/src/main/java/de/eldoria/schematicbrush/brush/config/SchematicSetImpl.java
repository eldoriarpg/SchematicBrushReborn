/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilderImpl;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.config.util.ValueProvider;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The schematic set represents a part of a brush, which will be combined to a brush by the {@link BrushSettingsImpl} A
 * brush can have a weight which indicates how often it should be used, when a {@link BrushSettingsImpl} contains more than
 * one brush. The brush contains one or more schematics which will be provided by random.
 */
public class SchematicSetImpl implements SchematicSet {

    private final List<Schematic> schematics;
    private final Selector selector;
    private final Map<? extends SchematicModifier, Mutator<?>> schematicModifier;
    private int weight;

    public SchematicSetImpl(Set<Schematic> schematics, Selector selector, Map<? extends SchematicModifier, Mutator<?>> schematicModifier, int weight) {
        this.schematics = new ArrayList<>(schematics);
        this.selector = selector;
        this.schematicModifier = schematicModifier;
        this.weight = weight;
    }

    /**
     * Get the mutator
     *
     * @param type type
     * @return mutator
     */
    @Override
    public Mutator<?> getMutator(SchematicModifier type) {
        return schematicModifier.get(type);
    }

    /**
     * Get a random schematic from the set
     *
     * @return schematic
     */
    @Override
    public Schematic getRandomSchematic() {
        if (schematics.isEmpty()) return null;

        Clipboard clipboard = null;

        Schematic randomSchematic = null;
        // Search for loadable schematic. Should be likely always the first one.
        while (clipboard == null && !schematics.isEmpty()) {
            randomSchematic = schematics.get(randomInt(schematics.size()));
            try {
                clipboard = randomSchematic.loadSchematic();
            } catch (IOException e) {
                // Silently fail and search for another schematic.
                SchematicBrushReborn.logger().info("Schematic \"" + randomSchematic.path() + "\" does not exist anymore.");
                schematics.remove(randomSchematic);
            }
        }

        return randomSchematic;
    }

    /**
     * Update a not weighted brush.
     *
     * @param weight weight to set.
     * @throws IllegalStateException    when the weight is not -1 and this method is called.
     * @throws IllegalArgumentException when the weight is less than 1.
     */
    @Override
    public void updateWeight(int weight) throws IllegalStateException, IllegalArgumentException {
        if (this.weight != -1) {
            throw new IllegalStateException("Weight can only be changed if it's the default value.");
        }

        if (weight < 1) {
            throw new IllegalArgumentException("Weight can't be less than 1");
        }

        this.weight = weight;
    }

    /**
     * Schematics
     *
     * @return schematics
     */
    @Override
    public List<Schematic> schematics() {
        return schematics;
    }

    /**
     * Weight of set
     *
     * @return weight
     */
    @Override
    public int weight() {
        return weight;
    }

    /**
     * Mutate the paste mutation with all mutators
     *
     * @param mutation mutation
     */
    @Override
    public void mutate(PasteMutation mutation) {
        schematicModifier.values().forEach(mod -> mod.invoke(mutation));
    }

    /**
     * Get the selector of the set.
     *
     * @return selector
     */
    @Override
    public Selector selector() {
        return selector;
    }

    /**
     * Convert the set into a builder
     *
     * @return new builder instance
     */
    @Override
    public SchematicSetBuilderImpl toBuilder() {
        var builder = new SchematicSetBuilderImpl(selector);
        schematicModifier.forEach(builder::withMutator);
        return builder;
    }

    /**
     * Refresh the values of all mutators
     */
    @Override
    public void refreshMutator() {
        schematicModifier.values().forEach(ValueProvider::refresh);
    }
}
