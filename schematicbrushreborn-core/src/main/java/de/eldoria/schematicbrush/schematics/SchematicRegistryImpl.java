/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.SchematicBrushReborn;
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
            throw new AlreadyRegisteredException("Cache with key " + key.name() + " is already registered via " + getCache(key).getClass().getName());
        }
        cache.init();
        SchematicBrushReborn.logger().info("Schematic cache " + key + " registered.");
        caches.put(key, cache);
    }

    /**
     * Unregister a cache
     *
     * @param key key
     */
    @Override
    public void unregister(Nameable key) {
        if (caches.remove(key) != null) {
            SchematicBrushReborn.logger().info("Schematic cache " + key + " unregistered.");
        }
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

    public void shutdown() {
        var storages = caches.entrySet().iterator();
        while (storages.hasNext()) {
            var entry = storages.next();
            entry.getValue().shutdown();
            storages.remove();
            SchematicBrushReborn.logger().info("Schematic cache " + entry.getKey() + " shutdown.");
        }
    }
}
