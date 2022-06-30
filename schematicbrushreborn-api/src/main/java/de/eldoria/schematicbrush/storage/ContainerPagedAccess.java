/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ContainerPagedAccess<T> {
    int size();

    default int pages(int size) {
        return (int) Math.ceil(size() / (double) size);
    }

    ;

    /**
     * Get a page from this container
     *
     * @param page page index. Zero based
     * @param size size of the page content
     * @return a list of entries of this page.
     */
    CompletableFuture<List<T>> page(int page, int size);
}
