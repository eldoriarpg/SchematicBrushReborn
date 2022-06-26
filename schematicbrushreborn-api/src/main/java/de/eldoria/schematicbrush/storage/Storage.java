/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

import java.util.concurrent.CompletableFuture;

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

    default CompletableFuture<Void> migrate(Storage storage) {
        var presets = presets().migrate(storage.presets());
        var brushes = brushes().migrate(storage.brushes());

        return CompletableFuture.allOf(presets, brushes);
    }
}