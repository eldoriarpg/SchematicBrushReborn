/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Randomable;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.util.List;

/**
 * A class holding a set of schematics with weight and {@link Mutator}
 */
public interface SchematicSet extends Randomable {
    /**
     * Get the mutator
     *
     * @param type type
     * @return mutator
     */
    Mutator<?> getMutator(SchematicModifier type);

    /**
     * Get a random schematic from the set
     *
     * @return schematic
     */
    Schematic getRandomSchematic();

    /**
     * Update a not weighted brush.
     *
     * @param weight weight to set.
     * @throws IllegalStateException    when the weight is not -1 and this method is called.
     * @throws IllegalArgumentException when the weight is less than 1.
     */
    void updateWeight(int weight) throws IllegalStateException, IllegalArgumentException;

    /**
     * Schematics
     *
     * @return schematics
     */
    List<Schematic> schematics();

    /**
     * Weight of set
     *
     * @return weight
     */
    int weight();

    /**
     * Mutate the paste mutation with all mutators
     *
     * @param mutation mutation
     */
    void mutate(PasteMutation mutation);

    /**
     * Convert the set into a builder
     *
     * @return new builder instance
     */
    SchematicSetBuilder toBuilder();

    /**
     * Refresh the values of all mutators
     */
    void refreshMutator();
}
