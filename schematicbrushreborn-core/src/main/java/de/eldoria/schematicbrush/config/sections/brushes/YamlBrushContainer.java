/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.brushes;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.storage.YamlContainerPagedAccess;
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
import java.util.concurrent.CompletableFuture;

@SerializableAs("sbrYamlBrushContainer")
public class YamlBrushContainer implements BrushContainer, ConfigurationSerializable {
    private final Map<String, Brush> brushes = new HashMap<>();

    public YamlBrushContainer(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Brush> brushList = map.getValueOrDefault("brushes", Collections.emptyList());
        brushList.forEach(p -> brushes.put(p.name(), p));
    }

    public YamlBrushContainer() {
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
        brushes.put(brush.name(), brush);
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
    public ContainerPagedAccess<Brush> paged() {
        return new YamlContainerPagedAccess<>(this);
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
}
