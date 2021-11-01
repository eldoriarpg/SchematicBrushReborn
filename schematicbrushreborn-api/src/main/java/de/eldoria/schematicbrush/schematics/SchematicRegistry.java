package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;

import java.util.HashMap;
import java.util.Map;

public class SchematicRegistry {
    private final Map<Nameable, SchematicCache> caches = new HashMap<>();

    /**
     * Gets a cache by key
     *
     * @param key key of cache
     * @return the cache. If the cache was not registered this will be null
     */
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
    public void unregister(Nameable key) {
        if (key.equals(SchematicCache.DEFAULT_CACHE)) {
            throw new AlreadyRegisteredException("Default cache can't be unregistered.");
        }
        caches.remove(key);
    }

    /**
     * Reloads all registered caches.
     */
    public void reload() {
        caches.values().forEach(SchematicCache::reload);
    }

    /**
     * Returns the total amount of registered schematics
     *
     * @return schematic count.
     */
    public int schematicCount() {
        return caches.values().stream().mapToInt(SchematicCache::schematicCount).sum();
    }

    /**
     * Returns the total amount of registered directories
     *
     * @return directory count
     */
    public int directoryCount() {
        return caches.values().stream().mapToInt(SchematicCache::directoryCount).sum();
    }
}
