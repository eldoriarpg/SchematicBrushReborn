/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.registry.Registry;

/**
 * A registry to register, manage and retrieve a {@link SchematicCache}.
 */
public interface SchematicRegistry extends Registry<Nameable, SchematicCache> {

    /**
     * Get the cache associated with this name
     *
     * @param key name
     * @return cache
     * @deprecated Replaced by {@link Registry#get(Nameable)}
     * @throws IllegalArgumentException if no entry is registered with this key.
     */
    @Deprecated(forRemoval = true)
    default SchematicCache getCache(Nameable key) {
        return get(key);
    }

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
