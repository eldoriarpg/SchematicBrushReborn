package de.eldoria.schematicbrush.registry;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseRegistry<T extends Nameable, V> implements Registry<T, V> {
    private final Map<T, V> registry = new HashMap<>();

    /**
     * Registers an enty with this key
     *
     * @param key   key
     * @param entry entry
     * @throws AlreadyRegisteredException When an entry with this key is already present.
     */
    @Override
    public void register(T key, V entry) {
        if (isRegistered(key)) {
            throw new AlreadyRegisteredException(String.format("Tried to register entry %s with key %s at %s, but this key is already used by %s.",
                    registry.getClass().getName(), key, getClass().getSimpleName(), get(key).getClass().getName()));
        }
        SchematicBrushReborn.logger().info("Registered entry of type " + key.name() + " with " + entry.getClass().getName());
        registry.put(key, entry);
    }

    /**
     * Unregisters any value associated with this key.
     *
     * @param key key
     */
    @Override
    public void unregister(T key) {
        if (registry.remove(key) != null) {
            SchematicBrushReborn.logger().info("Entry " + key.name() + " unregistered at " + getClass().getSimpleName());
        } else {
            SchematicBrushReborn.logger().info("Attempted to unregister entry " + key.name() + " at " + getClass().getSimpleName() + ", but entry was not registered.");
        }
    }

    /**
     * Returns the value associated with this key
     *
     * @param key key
     * @return value
     * @throws IllegalArgumentException if no entry is registered witht his key.
     */
    @Override
    public V get(T key) {
        var value = registry.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No entry with key " + key + " registered at " + getClass().getSimpleName());
        }
        return value;
    }

    /**
     * Checks if an entry with this key is registered
     *
     * @param key key
     * @return true if registered
     */
    @Override
    public boolean isRegistered(T key) {
        return registry.containsKey(key);
    }

    /**
     * Get a map of the registered entries
     *
     * @return unmodifiable map
     */
    @Override
    public Map<T, V> registry() {
        return Collections.unmodifiableMap(registry);
    }
}
