/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.registry.BaseRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StorageRegistryImpl extends BaseRegistry<Nameable, Storage> implements StorageRegistry {
    private final Storage defaultStorage;
    private final Configuration configuration;

    public StorageRegistryImpl(Storage defaultStorage, Configuration configuration) {
        this.defaultStorage = defaultStorage;
        this.configuration = configuration;
    }

    @Override
    @NotNull
    public Storage activeStorage() {
        if (!isRegistered(configuration.general().storageType())) {
            SchematicBrushReborn.logger().warning("Storage type " + configuration.general().storageType() + " not registered. Using fallback YAML storage.");
            SchematicBrushReborn.logger().warning("Available storage types are: " + registry().keySet().stream().map(Nameable::name).collect(Collectors.joining(", ")));
            return defaultStorage;
        }
        return get(configuration.general().storageType());
    }

    @Override
    public CompletableFuture<Void> migrate(Nameable source, Nameable target) {
        var sourceStorage = get(source);
        var targetStorage = get(target);

        if (sourceStorage == null) throw new IllegalArgumentException("Storage " + source + " does not exist.");
        if (targetStorage == null) throw new IllegalArgumentException("Storage " + target + " does not exist.");

        return CompletableFuture.runAsync(() -> targetStorage.migrate(sourceStorage));
    }

    public void shutdown() {
        for (var nameable : new HashSet<>(registry().keySet())) {
            get(nameable).shutdown();
            SchematicBrushReborn.logger().info("Storage " + nameable + " shutdown.");
            unregister(nameable);
        }
    }

    @Override
    protected String name() {
        return "Storage Registry";
    }
}
