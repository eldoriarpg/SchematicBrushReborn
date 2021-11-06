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

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class SchematicRegistryImpl implements SchematicRegistry {
    private final Map<Nameable, SchematicCache> caches = new HashMap<>();

    /**
     * Gets a cache by key
     *
     * @param key key of cache
     * @return the cache. If the cache was not registered this will be null
     */
    @Override
    public SchematicCache getCache(Nameable key) {
        return caches.get(key);
    }

    /**
     * Registers a cache
     *
     * @param key   key
     * @param cache cache
     * @throws AlreadyRegisteredException when a cache with this key is already registered
     */
    @Override
    public void register(Nameable key, SchematicCache cache) {
        if (caches.containsKey(key)) {
            throw new AlreadyRegisteredException("Cache with key " + key.name() + " is already registered");
        }
        cache.init();
        caches.put(key, cache);
    }

    /**
     * Unregister a cache
     *
     * @param key key
     */
    @Override
    public void unregister(Nameable key) {
        if (key.equals(SchematicCache.DEFAULT_CACHE)) {
            throw new AlreadyRegisteredException("Default cache can't be unregistered.");
        }
        caches.remove(key);
    }

    /**
     * Reloads all registered caches.
     */
    @Override
    public void reload() {
        caches.values().forEach(SchematicCache::reload);
    }

    /**
     * Returns the total amount of registered schematics
     *
     * @return schematic count.
     */
    @Override
    public int schematicCount() {
        return caches.values().stream().mapToInt(SchematicCache::schematicCount).sum();
    }

    /**
     * Returns the total amount of registered directories
     *
     * @return directory count
     */
    @Override
    public int directoryCount() {
        return caches.values().stream().mapToInt(SchematicCache::directoryCount).sum();
    }
}
