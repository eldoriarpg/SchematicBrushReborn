/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.brush;

import de.eldoria.schematicbrush.storage.base.Container;

import java.io.Closeable;
import java.io.IOException;

/**
 * Container used to store {@link Brush}es.
 */
public interface BrushContainer extends Closeable, Container<Brush> {

    @Override
    default void close() throws IOException {

    }
}
