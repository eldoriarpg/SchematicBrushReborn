/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a value provider.
 *
 * @param <T> type of value
 */
public interface ValueProvider<T> {
    /**
     * Change the current value.
     *
     * @param value value to set
     */
    void value(@NotNull T value);

    /**
     * Get the current value
     *
     * @return the value
     */
    @NotNull
    T value();

    /**
     * Returns a new value
     *
     * @return new value
     */
    T valueProvider();

    /**
     * Refresh the current value by calling the {@link ValueProvider#valueProvider()}
     */
    default void refresh() {
        value(valueProvider());
    }
}
