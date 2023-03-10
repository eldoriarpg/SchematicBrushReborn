/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.preset;

import de.eldoria.schematicbrush.storage.base.Container;

import java.io.Closeable;
import java.io.IOException;

/**
 * Container used to store {@link Preset}s.
 */
public interface PresetContainer extends Closeable, Container<Preset> {

    @Override
    default void close() throws IOException {
    }
}
