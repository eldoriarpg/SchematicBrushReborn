/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.brush.config.util.Nameable;

import java.util.HashMap;
import java.util.Map;

public class StorageRegistryImpl implements StorageRegistry {
    private final Map<Nameable, Storage> storages = new HashMap<>();

    @Override
    public Storage getRegistry(Nameable key) {
        return storages.get(key);
    }

    @Override
    public void register(Nameable key, Storage storage) {
        if (storages.containsKey(key)) {
            throw new IllegalStateException(String.format("Tried to register storage %s with key %s, but this key is already used by %s.",
                    storage.getClass().getName(), key, getRegistry(key).getClass().getName()));
        }
        storages.put(key, storage);
    }

    @Override
    public void unregister(Nameable key) {
        storages.remove(key);
    }

    @Override
    public void migrate(Nameable source, Nameable target) {
        var sourceStorage = getRegistry(source);
        var targetStorage = getRegistry(target);


    }
}
