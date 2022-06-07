/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.preset;

import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class PresetStorageImpl implements PresetStorage {
    private final Map<Nameable, Presets> storages = new HashMap<>();

    @Override
    public Presets getRegistry(Nameable key) {
        return storages.get(key);
    }

    /**
     * Registers a new storage type
     *
     * @param key     key
     * @param storage the storage access provider
     */
    @Override
    public void register(Nameable key, Presets storage) {
        storages.put(key, storage);
    }

    /**
     * Unregisters a storage type
     *
     * @param key key
     */
    @Override
    public void unregister(Nameable key) {
        storages.remove(key);
    }

    @Override
    public void migrate(Nameable source, Nameable target) {
        var sourceRegistry = getRegistry(source);
        var targetRegistry = getRegistry(target);
        sourceRegistry.globalContainer().getPresets()
                .whenComplete(Futures.whenComplete(presets -> {
                    for (var preset : presets) {
                        targetRegistry.globalContainer().add(preset)
                                .whenComplete(Futures.whenComplete(
                                        succ -> SchematicBrushReborn.logger().info(
                                                "Migrated global preset " + preset.name() + "."),
                                        err -> SchematicBrushReborn.logger().log(Level.WARNING,
                                                "Migration of global preset " + preset.name() + " failed.", err)));
                    }
                }, err -> SchematicBrushReborn.logger().log(Level.WARNING, "Could not read schematics from source.", err)));

        sourceRegistry.getPlayerPresets()
                .whenComplete(Futures.whenComplete(container -> {
                    for (var entry : container.entrySet()) {
                        var playerContainer = targetRegistry.playerContainer(entry.getKey());
                        entry.getValue().getPresets()
                                .whenComplete(Futures.whenComplete(presets -> {
                                    for (var preset : presets) {
                                        playerContainer.add(preset)
                                                .whenComplete(Futures.whenComplete(
                                                        succ -> SchematicBrushReborn.logger().info(
                                                                String.format("Migrated preset %s of player %s.", preset.name(), entry.getKey())),
                                                        err -> SchematicBrushReborn.logger().log(Level.WARNING,
                                                                String.format("Migration of %s of player %s failed.", preset.name(), entry.getKey()), err)));
                                    }
                                }, err -> SchematicBrushReborn.logger().log(Level.WARNING, "Could not read schematics from source.", err)));
                    }
                }, err -> SchematicBrushReborn.logger().log(Level.WARNING, "Could not read player presets")));
    }
}
