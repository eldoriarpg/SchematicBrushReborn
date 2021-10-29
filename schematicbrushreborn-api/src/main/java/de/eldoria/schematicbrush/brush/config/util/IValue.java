package de.eldoria.schematicbrush.brush.config.util;

public interface IValue<T> {
    void value(T value);

    T value();

    T valueProvider();

    default void refresh() {
        value(valueProvider());
    }
}
