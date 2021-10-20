package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;
import de.eldoria.schematicbrush.util.ComponentProvider;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface Mutator<T> extends IShiftable<T>, ConfigurationSerializable, ComponentProvider {
    void invoke(PasteMutation mutation);

    String name();
}
