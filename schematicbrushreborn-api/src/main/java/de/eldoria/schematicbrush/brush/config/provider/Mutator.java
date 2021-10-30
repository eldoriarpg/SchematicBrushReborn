package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.brush.config.util.IShiftable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Interface to implement a mutator to mutate a {@link PasteMutation}
 * @param <T> value type of mutator
 */
public interface Mutator<T> extends IShiftable<T>, ConfigurationSerializable, ComponentProvider {
    void invoke(PasteMutation mutation);
}
