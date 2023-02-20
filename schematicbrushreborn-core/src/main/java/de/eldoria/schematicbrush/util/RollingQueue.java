/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RollingQueue<T> {
    private final int size;
    private final ConcurrentLinkedQueue<T> queue;

    public RollingQueue(int size) {
        this.size = size;
        queue = new ConcurrentLinkedQueue<>();
    }

    public void add(@NotNull T t) {
        synchronized (queue) {
            if (size == queue.size()) queue.remove();
            queue.add(t);
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized List<T> flush() {
        synchronized (queue) {
            var cache = new ArrayList<>(queue);
            queue.clear();
            return cache;
        }
    }

    public Collection<T> values() {
        return queue.stream().toList();
    }

    public void clear() {
        queue.clear();
    }
}
