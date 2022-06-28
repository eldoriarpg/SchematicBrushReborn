package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.storage.base.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class YamlContainerPagedAccess<T> implements ContainerPagedAccess<T> {
    private final Container<T> container;

    public YamlContainerPagedAccess(Container<T> container) {
        this.container = container;
    }

    @Override
    public int size() {
        return container.all().join().size();
    }

    @Override
    public CompletableFuture<List<T>> page(int page, int size) {
        return container.all().thenApply(entries -> {
            var join = new ArrayList<>(entries);
            return join.subList(page * size, Math.min(join.size(), (page + 1) * size));
        });
    }
}
