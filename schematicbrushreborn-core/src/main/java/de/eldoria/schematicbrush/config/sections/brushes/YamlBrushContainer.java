/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.brushes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.storage.YamlContainerPagedAccess;
import de.eldoria.schematicbrush.storage.base.Container;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SerializableAs("sbrYamlBrushContainer")
public class YamlBrushContainer implements BrushContainer, ConfigurationSerializable {
    private UUID uuid;
    private final Map<String, Brush> brushes;

    public YamlBrushContainer(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        uuid = UUID.fromString(map.getValueOrDefault("uuid", Container.GLOBAL.toString()));
        List<Brush> brushList = map.getValueOrDefault("brushes", Collections.emptyList());
        brushes = new HashMap<>();
        brushList.forEach(p -> brushes.put(p.name().toLowerCase(Locale.ROOT), p));
    }

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
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("brushes", new ArrayList<>(brushes.values()))
                .build();
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
