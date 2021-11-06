/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
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
}
