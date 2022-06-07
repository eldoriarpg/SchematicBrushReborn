/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;

public interface SchematicCacheHolder {
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
}
