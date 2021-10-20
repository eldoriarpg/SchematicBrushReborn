package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface Mutator<T> extends IShiftable<T>, ConfigurationSerializable {
    void invoke(PasteMutation mutation);
}
