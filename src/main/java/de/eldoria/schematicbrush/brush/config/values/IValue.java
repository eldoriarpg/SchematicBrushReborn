package de.eldoria.schematicbrush.brush.config.values;

public interface IValue<T> {
    void value(T value);

    T value();

    T valueProvider();

    default void refresh() {
        value(valueProvider());
    }
}
