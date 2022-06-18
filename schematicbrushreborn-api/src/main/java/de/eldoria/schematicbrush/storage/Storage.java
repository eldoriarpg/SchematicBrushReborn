/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

public interface Storage {
    /**
     * Get the preset storage
     *
     * @return preset storage
     */
    Presets presets();

    /**
     * Get the brushes storage
     *
     * @return brushes storages
     */
    Brushes brushes();

    default void migrate(Storage storage) {
        presets().migrate(storage.presets());
        brushes().migrate(storage.brushes());
    }
}
