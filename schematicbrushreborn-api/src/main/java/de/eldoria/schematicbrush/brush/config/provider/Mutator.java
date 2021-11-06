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

package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.brush.config.util.Shiftable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Interface to implement a mutator to mutate a {@link PasteMutation}
 *
 * @param <T> value type of mutator
 */
public interface Mutator<T> extends Shiftable<T>, ConfigurationSerializable, ComponentProvider {
    /**
     * Invoke the mutator on a paste mutation. The mutation will be applied on the brush.
     *
     * @param mutation mutation
     */
    void invoke(PasteMutation mutation);
}
