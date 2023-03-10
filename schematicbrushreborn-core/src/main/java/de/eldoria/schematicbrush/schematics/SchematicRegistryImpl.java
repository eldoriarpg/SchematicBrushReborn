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

@SuppressWarnings("unused")
public class SchematicRegistryImpl extends BaseRegistry<Nameable, SchematicCache> implements SchematicRegistry {


    @Override
    public void register(Nameable key, SchematicCache entry) {
        super.register(key, entry);
        entry.init();
    }

    /**
     * Reloads all registered caches.
     */
    @Override
    public void reload() {
        registry().values().forEach(SchematicCache::reload);
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
