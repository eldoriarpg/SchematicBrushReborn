/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

/**
 * An alternative to the {@link Object#clone()} call which provides a usually cleaner implementation.
 */
public interface Copyable {
    /**
     * Returns a copy of the implementing object.
     * <p>
     * The copy should be by value and should not contain any mutable references to other objects.
     *
     * @return a copy of the object instance
     */
    Object copy();
}
