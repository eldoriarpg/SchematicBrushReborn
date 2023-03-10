/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.registry;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseRegistry<T extends Nameable, V> implements Registry<T, V> {
    private final Map<T, V> registry = new HashMap<>();

    @Override
    public void register(T key, V entry) {
        if (isRegistered(key)) {
            throw new AlreadyRegisteredException(String.format("Tried to register entry %s with key %s at %s, but this key is already used by %s.",
                    entry.getClass().getName(), key, name(), get(key).getClass().getName()));
        }
        SchematicBrushReborn.logger().info(String.format("Registered entry of type %s with %s at %s", key, entry.getClass().getName(), name()));
        registry.put(key, entry);
    }

    @Override
    public void unregister(T key) {
        if (registry.remove(key) != null) {
            SchematicBrushReborn.logger().info("Entry " + key + " unregistered at " + name());
        } else {
            SchematicBrushReborn.logger().info("Attempted to unregister entry " + key + " at " + name() + ", but entry was not registered.");
        }
    }

    @Override
    public V get(T key) {
        var value = registry.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No entry with key " + key + " registered at " + name());
        }
        return value;
    }

    @Override
    public boolean isRegistered(T key) {
        return registry.containsKey(key);
    }

    @Override
    public Map<T, V> registry() {
        return Collections.unmodifiableMap(registry);
    }

    /**
     * Get a name for the registry
     *
     * @return registry name
     */
    protected String name() {
        return getClass().getSimpleName();
    }
}
