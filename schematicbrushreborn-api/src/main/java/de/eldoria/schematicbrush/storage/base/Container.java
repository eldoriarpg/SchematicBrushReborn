/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * An interface which represents a container which holds named entries of any type.
 *
 * @param <T> Type of contained entries.
 */
public interface Container<T> {
    UUID GLOBAL = new UUID(0L, 0L);

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
     *
     * @return container pages
     */
    CompletableFuture<? extends ContainerPagedAccess<T>> paged();

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
     * Get the amount of entries in this container.
     *
     * @return A future providing the size of the container.
     */
    CompletableFuture<Integer> size();

    /**
     * Get the owner of the container.
     * <p>
     * This might be the uuid of the owning player or {@link #GLOBAL} when it is a global container.
     *
     * @return owner uuid
     */
    @NotNull
    UUID owner();

    /**
     * Checks if the {@link #owner()} is equal to {@link #GLOBAL}
     *
     * @return true if this container has the global UUID
     */
    @JsonIgnore
    default boolean isGlobalContainer() {
        return GLOBAL.equals(owner());
    }

    /**
     * Migrate the container into this container.
     * This will override entries if they already exist with the same name.
     * This will not remove already existing entries.
     *
     * @param container container with entries to add.
     */
    default void migrate(Container<T> container) {
        var entries = container.all().join();
        for (var entry : entries) {
            add(entry).join();
        }
    }
}
