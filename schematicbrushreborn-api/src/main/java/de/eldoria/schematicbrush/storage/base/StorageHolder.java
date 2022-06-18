/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.base;

import de.eldoria.schematicbrush.brush.config.util.Nameable;

public interface StorageHolder<T> {
    T getRegistry(Nameable key);

    /**
     * Registers a new storage type
     *
     * @param key     key
     * @param storage the storage access provider
     */
    void register(Nameable key, T storage);

    /**
     * Unregisters a storage type
     *
     * @param key key
     */
    void unregister(Nameable key);

    void migrate(Nameable source, Nameable target);
}
