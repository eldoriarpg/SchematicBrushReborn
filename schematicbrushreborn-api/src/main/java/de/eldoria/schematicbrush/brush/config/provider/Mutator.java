/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.brush.config.util.Copyable;
import de.eldoria.schematicbrush.brush.config.util.Shiftable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Interface to implement a mutator to mutate a {@link PasteMutation}
 *
 * @param <T> value type of mutator
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@clazz")
public interface Mutator<T> extends Shiftable<T>, ConfigurationSerializable, ComponentProvider, Copyable {
    /**
     * Invoke the mutator on a paste mutation. The mutation will be applied on the brush.
     *
     * @param mutation mutation
     */
    void invoke(PasteMutation mutation);

    @Override
    Mutator<T> copy();
}
