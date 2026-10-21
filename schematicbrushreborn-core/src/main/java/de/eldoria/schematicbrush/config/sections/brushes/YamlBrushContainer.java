/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.brushes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.storage.YamlContainerPagedAccess;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlBrushContainer implements BrushContainer {
    private UUID uuid;
    private final Map<String, Brush> brushes;

    public YamlBrushContainer(UUID uuid) {
        brushes = new HashMap<>();
        this.uuid = uuid;
    }

    @JsonCreator
    public YamlBrushContainer(@JsonProperty("uuid") UUID uuid,
                              @JsonProperty("brushes") Map<String, Brush> brushes) {
        this.brushes = brushes;
        this.uuid = uuid;
    }

    @Override
    public CompletableFuture<Optional<Brush>> get(String name) {
        return CompletableFuture.completedFuture(Optional.ofNullable(brushes.get(name.toLowerCase(Locale.ROOT))));
    }

    @Override
    public CompletableFuture<Void> add(Brush brush) {
        brushes.put(brush.name().toLowerCase(Locale.ROOT), brush);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return CompletableFuture.completedFuture(brushes.remove(name.toLowerCase(Locale.ROOT)) != null);
    }

    @Override
    public CompletableFuture<Collection<Brush>> all() {
        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(brushes.values()));
    }

    @Override
    public CompletableFuture<ContainerPagedAccess<Brush>> paged() {
        return CompletableFuture.completedFuture(new YamlContainerPagedAccess<>(this));
    }

    /**
     * Returns all names in this preset container
     *
     * @return set of names
     */
    @Override
    public Set<String> names() {
        return brushes.keySet();
    }

    @Override
    public CompletableFuture<Integer> size() {
        return CompletableFuture.completedFuture(brushes.size());
    }

    @Override
    public @NotNull UUID owner() {
        return uuid;
    }

    public YamlBrushContainer updateOwner(UUID uuid) {
        this.uuid = uuid;
        return this;
    }
}
