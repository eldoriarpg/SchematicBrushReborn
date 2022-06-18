/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.base;

import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.SchematicBrushReborn;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public interface Container<T> {
    /**
     * Get a entry by name
     *
     * @param name name of entry
     * @return optional containing the entry if found
     */
    CompletableFuture<Optional<T>> get(String name);

    /**
     * Add an entry
     *
     * @param preset entry to add
     */
    CompletableFuture<Void> add(T preset);

    /**
     * Get all presets in this container
     *
     * @return unmodifiable collection
     */
    CompletableFuture<Collection<T>> all();

    /**
     * Remove a entry by name
     *
     * @param name name of entry
     * @return true if the entry was removed
     */
    CompletableFuture<Boolean> remove(String name);

    /**
     * Returns all names in this container
     *
     * @return set of names
     */
    Set<String> names();

    default void migrate(Container<T> container) {
        container.all().whenComplete(Futures.whenComplete(entries -> {
            for (var entry : entries)
                add(entry).whenComplete(Futures.whenComplete(
                        res -> {
                        }, err -> SchematicBrushReborn.logger().log(Level.SEVERE, "Could not save player container", err)));
        }, err -> SchematicBrushReborn.logger().log(Level.SEVERE, "Could not load player container", err)));
    }
}
