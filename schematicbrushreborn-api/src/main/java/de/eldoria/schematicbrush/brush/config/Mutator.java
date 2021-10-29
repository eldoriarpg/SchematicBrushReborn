package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.brush.config.util.IShiftable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface Mutator<T> extends IShiftable<T>, ConfigurationSerializable, ComponentProvider {
    void invoke(PasteMutation mutation);
}
