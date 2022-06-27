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
                    registry.getClass().getName(), key, getClass().getSimpleName(), get(key).getClass().getName()));
        }
        SchematicBrushReborn.logger().info("Registered entry of type " + key.name() + " with " + entry.getClass().getName());
        registry.put(key, entry);
    }

    @Override
    public void unregister(T key) {
        if (registry.remove(key) != null) {
            SchematicBrushReborn.logger().info("Entry " + key.name() + " unregistered at " + getClass().getSimpleName());
        } else {
            SchematicBrushReborn.logger().info("Attempted to unregister entry " + key.name() + " at " + getClass().getSimpleName() + ", but entry was not registered.");
        }
    }

    @Override
    public V get(T key) {
        var value = registry.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No entry with key " + key + " registered at " + getClass().getSimpleName());
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
}
