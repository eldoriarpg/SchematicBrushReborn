package de.eldoria.schematicbrush.brush.config.values;

public interface IShiftable<T> extends IValue<T> {
    default T shift() {
        value(valueProvider());
        return value();
    }
}
