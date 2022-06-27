package de.eldoria.schematicbrush.registry;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;

import java.util.Map;

public interface Registry<T extends Nameable, V> {
    /**
     * Registers an enty with this key
     *
     * @param key   key
     * @param entry entry
     * @throws AlreadyRegisteredException When an entry with this key is already present.
     */
    void register(T key, V entry);

    /**
     * Unregisters any value associated with this key.
     *
     * @param key key
     */
    void unregister(T key);

    /**
     * Returns the value associated with this key
     *
     * @param key key
     * @return value
     * @throws IllegalArgumentException if no entry is registered witht his key.
     */
    V get(T key);

    /**
     * Checks if an entry with this key is registered
     *
     * @param key key
     * @return true if registered
     */
    boolean isRegistered(T key);

    /**
     * Get a map of the registered entries
     *
     * @return unmodifiable map
     */
    Map<T, V> registry();
}
