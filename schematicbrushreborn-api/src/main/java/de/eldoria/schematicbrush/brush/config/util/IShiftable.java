package de.eldoria.schematicbrush.brush.config.util;

public interface IShiftable<T> extends IValue<T> {
    /**
     * Shifts the value to the next.
     */
    default void shift() {
        value(valueProvider());
    }
}
