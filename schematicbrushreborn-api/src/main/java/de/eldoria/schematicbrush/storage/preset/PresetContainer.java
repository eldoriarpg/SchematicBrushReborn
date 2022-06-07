/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.preset;

import java.io.Closeable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface PresetContainer extends Closeable {
    /**
     * Get a preset by name
     *
     * @param name name of preset
     * @return optional containing the preset if found
     */
    CompletableFuture<Optional<Preset>> get(String name);

    /**
     * Add a preset
     *
     * @param preset preset to add
     */
    CompletableFuture<Void> add(Preset preset);

    /**
     * Get all presets in this container
     *
     * @return unmodifiable collection
     */
    CompletableFuture<Collection<Preset>> getPresets();

    /**
     * Remove a preset by name
     *
     * @param name name of preset
     * @return true if the preset was removed
     */
    CompletableFuture<Boolean> remove(String name);

    /**
     * Returns all names in this preset container
     *
     * @return set of names
     */
    Set<String> names();
}
