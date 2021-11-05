package de.eldoria.schematicbrush.brush.config.util;

/**
 * Represents a shiftable {@link ValueProvider}
 *
 * @param <T> type of value
 */
public interface Shiftable<T> extends ValueProvider<T> {
    /**
     * Shifts the value to the next.
     */
    default void shift() {
        value(valueProvider());
    }
}
