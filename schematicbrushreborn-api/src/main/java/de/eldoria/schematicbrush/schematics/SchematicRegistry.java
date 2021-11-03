package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;

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
