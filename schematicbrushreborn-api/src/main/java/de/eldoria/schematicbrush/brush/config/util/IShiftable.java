package de.eldoria.schematicbrush.brush.config.util;

public interface IShiftable<T> extends IValue<T> {
    /**
     * Shifts the value to the next.
     *
     * @return the following value of the current value
     */
    default T shift() {
        value(valueProvider());
        return value();
    }
}
