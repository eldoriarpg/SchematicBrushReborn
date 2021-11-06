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

package de.eldoria.schematicbrush.brush.config.util;

/**
 * Represents a value provider.
 *
 * @param <T> type of value
 */
public interface ValueProvider<T> {
    /**
     * Change the current value.
     *
     * @param value value to set
     */
    void value(T value);

    /**
     * Get the current value
     *
     * @return the value
     */
    T value();

    /**
     * Returns a new value
     *
     * @return new value
     */
    T valueProvider();

    /**
     * Refresh the current value by calling the {@link ValueProvider#valueProvider()}
     */
    default void refresh() {
        value(valueProvider());
    }
}
