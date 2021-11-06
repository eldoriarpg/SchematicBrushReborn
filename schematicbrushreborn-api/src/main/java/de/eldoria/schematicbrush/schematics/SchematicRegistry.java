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

package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;

/**
 * A registry to register, manage and retrieve a {@link SchematicCache}.
 */
public interface SchematicRegistry {
    /**
     * Gets a cache by key
     *
     * @param key key of cache
     * @return the cache. If the cache was not registered this will be null
     */
    SchematicCache getCache(Nameable key);

    /**
     * Registers a cache
     *
     * @param key   key
     * @param cache cache
     * @throws AlreadyRegisteredException when a cache with this key is already registered
     */
    void register(Nameable key, SchematicCache cache);

    /**
     * Unregister a cache
     *
     * @param key key
     */
    void unregister(Nameable key);

    /**
     * Reloads all registered caches.
     */
    void reload();

    /**
     * Returns the total amount of registered schematics
     *
     * @return schematic count.
     */
    int schematicCount();

    /**
     * Returns the total amount of registered directories
     *
     * @return directory count
     */
    int directoryCount();
}
