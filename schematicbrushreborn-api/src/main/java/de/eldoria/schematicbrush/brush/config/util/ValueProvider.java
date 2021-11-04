package de.eldoria.schematicbrush.brush.config.util;

/**
 * Represents a value provider.
 *
 * @param <T> type of value
 */
public interface ValueProvider<T> {
    /**
     * Change the current value.
     *
     * @param value value to set
     */
    void value(T value);

    /**
     * Get the current value
     *
     * @return the value
     */
    T value();

    /**
     * Returns a new value
     *
     * @return new value
     */
    T valueProvider();

    /**
     * Refresh the current value by calling the {@link ValueProvider#valueProvider()}
     */
    default void refresh() {
        value(valueProvider());
    }
}
