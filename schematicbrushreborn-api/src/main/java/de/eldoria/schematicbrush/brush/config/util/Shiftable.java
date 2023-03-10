/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

/**
 * Represents a shiftable {@link ValueProvider}
 *
 * @param <T> type of value
 */
public interface Shiftable<T> extends ValueProvider<T> {
    /**
     * Shifts the value to the next.
     */
    default void shift() {
        value(valueProvider());
    }

    /**
     * Indicates if a value is shiftable
     *
     * @return true if it is shiftable
     */
    default boolean shiftable() {
        return false;
    }
}
