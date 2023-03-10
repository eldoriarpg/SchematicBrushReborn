/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.storage.base.Container;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An interface which represents paged access to a {@link Container}.
 *
 * @param <T> Type of the container entries.
 */
public interface ContainerPagedAccess<T> {
    /**
     * Total amount of entries in this container.
     * <p>
     * This value should change when the container size changes.
     * For example in case of addition or removal of an entry.
     *
     * @return the current size of the container
     */
    int size();

    /**
     * Amount of pages.
     * <p>
     * The ceiled value of a division of {@link #size()} and the input parameter.
     *
     * @param size size of a page
     * @return the amount of pages
     */
    default int pages(int size) {
        return (int) Math.ceil(size() / (double) size);
    }

    /**
     * Get a page from this container.
     * <p>
     * A page is considered a subset of all saved entries.
     * The order of the entries should be fixed and ideally defined by being ordered by some named identifier.
     *
     * @param page page index. Zero based
     * @param size size of the page content
     * @return a list of entries of this page.
     */
    CompletableFuture<List<T>> page(int page, int size);
}
