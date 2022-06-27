/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

import java.util.concurrent.CompletableFuture;

/**
 * Interface which represents a storage which provides implementations for {@link Presets} and {@link Brushes}.
 */
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

    /**
     * Migrate the storage into this storage.
     * This will override entries if they already exist whith the same name.
     * This will not remove already existing entries.
     *
     * @param storage storage with entries to add.
     * @return A future which completes when all underlying processes are completed.
     */

    default CompletableFuture<Void> migrate(Storage storage) {
        var presets = presets().migrate(storage.presets());
        var brushes = brushes().migrate(storage.brushes());

        return CompletableFuture.allOf(presets, brushes);
    }

    /**
     * Attempts to save the storage.
     * <p>
     * Only required for file based storages.
     */
    default void save() {
    }

    /**
     * Called when the plugin shuts down and the storages get unregistered.
     */
    default void shutdown() {

    }
}
