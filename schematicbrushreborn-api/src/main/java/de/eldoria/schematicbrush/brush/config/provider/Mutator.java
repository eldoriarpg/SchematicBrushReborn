package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.brush.config.util.Shiftable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Interface to implement a mutator to mutate a {@link PasteMutation}
 *
 * @param <T> value type of mutator
 */
public interface Mutator<T> extends Shiftable<T>, ConfigurationSerializable, ComponentProvider {
    /**
     * Invoke the mutator on a paste mutation. The mutation will be applied on the brush.
     *
     * @param mutation mutation
     */
    void invoke(PasteMutation mutation);
}
