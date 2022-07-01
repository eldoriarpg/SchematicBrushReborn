/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.base;

import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * An interface which represents a container which holds named entries of any type.
 *
 * @param <T> Type of contained entries.
 */
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
     * @return A future which will completes once the entry was added
     */
    CompletableFuture<Void> add(T preset);

    /**
     * Get all presets in this container
     *
     * @return A future which contains an unmodifiable collection of all entries.
     */
    CompletableFuture<Collection<T>> all();

    /**
     * Returns an object which should provide access to entry pages of this container.
     * @return container pages
     */
    CompletableFuture<ContainerPagedAccess<T>> paged();

    /**
     * Remove a entry by name
     *
     * @param name name of entry
     * @return A future which completes once the entry got remove. Will be true if the entry was removed.
     */
    CompletableFuture<Boolean> remove(String name);

    /**
     * Returns all names in this container
     *
     * @return set of names
     */
    Set<String> names();

    /**
     * Migrate the container into this container.
     * This will override entries if they already exist whith the same name.
     * This will not remove already existing entries.
     *
     * @param container container with entries to add.
     * @return A future which completes when all underlying processes are completed.
     */
    default CompletableFuture<Void> migrate(Container<T> container) {
        List<CompletableFuture<?>> migrations = new ArrayList<>();
        var migrate = container.all()
                .whenComplete(Futures.whenComplete(entries -> {
                    for (var entry : entries) {
                        var migration = add(entry);
                        migrations.add(migration);
                        add(entry).whenComplete(Futures.whenComplete(
                                res -> {
                                }, err -> SchematicBrushReborn.logger().log(Level.SEVERE, "Could not save player container", err)));
                    }
                }, err -> SchematicBrushReborn.logger().log(Level.SEVERE, "Could not load player container", err)));
        migrations.add(migrate);
        return CompletableFuture.allOf(migrations.toArray(CompletableFuture[]::new));
    }
}
