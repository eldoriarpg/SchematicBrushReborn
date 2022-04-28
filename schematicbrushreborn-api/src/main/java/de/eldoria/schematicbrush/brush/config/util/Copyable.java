/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.util;

public interface Copyable {
    /**
     * Returns a copy of the implementing object.
     *
     * The copy should be by value and should not contain any mutable references to other objects.
     *
     * @return a copy of the object instance
     */
    Object copy();
}
