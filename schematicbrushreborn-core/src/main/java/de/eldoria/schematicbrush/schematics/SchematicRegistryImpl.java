/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.registry.BaseRegistry;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class SchematicRegistryImpl extends BaseRegistry<Nameable, SchematicCache> implements SchematicRegistry {
    private CompletableFuture<Void> runningReload = CompletableFuture.completedFuture(null);

    /**
     * Registers the cache and starts its initial load. A reload requested while that load runs waits for it.
     */
    @Override
    public synchronized void register(Nameable key, SchematicCache entry) {
        super.register(key, entry);
        runningReload = CompletableFuture.allOf(runningReload, entry.init());
    }

    /**
     * Reloads all registered caches.
     * <p>
     * While a reload is running, further calls return that reload instead of starting another one.
     */
    @Override
    public synchronized CompletableFuture<Void> reload() {
        if (!runningReload.isDone()) return runningReload;
        var reloads = registry().values().stream().map(SchematicCache::reload).toArray(CompletableFuture[]::new);
        runningReload = CompletableFuture.allOf(reloads);
        return runningReload;
    }

    /**
     * Returns the total amount of registered schematics
     *
     * @return schematic count.
     */
    @Override
    public int schematicCount() {
        return registry().values().stream().mapToInt(SchematicCache::schematicCount).sum();
    }

    /**
     * Returns the total amount of registered directories
     *
     * @return directory count
     */
    @Override
    public int directoryCount() {
        return registry().values().stream().mapToInt(SchematicCache::directoryCount).sum();
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
        return "Schematic Registry";
    }
}
