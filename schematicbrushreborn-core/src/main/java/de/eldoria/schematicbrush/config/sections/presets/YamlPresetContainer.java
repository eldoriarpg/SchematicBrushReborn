/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.storage.YamlContainerPagedAccess;
import de.eldoria.schematicbrush.storage.base.Container;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
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

@SerializableAs("sbrYamlPresetContainer")
public class YamlPresetContainer implements PresetContainer, ConfigurationSerializable {
    private UUID uuid;
    private final Map<String, Preset> presets;

    public YamlPresetContainer(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        uuid = UUID.fromString(map.getValueOrDefault("uuid", Container.GLOBAL.toString()));
        List<Preset> presetList = map.getValueOrDefault("presets", Collections.emptyList());
        presets = new HashMap<>();
        presetList.forEach(p -> presets.put(p.name().toLowerCase(Locale.ROOT), p));
    }

    @JsonCreator
    public YamlPresetContainer(@JsonProperty("uuid") UUID uuid,
                               @JsonProperty("presets") Map<String, Preset> presets) {
        this.uuid = uuid;
        this.presets = presets;
    }

    public YamlPresetContainer(UUID uuid) {
        this.uuid = uuid;
        presets = new HashMap<>();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("presets", new ArrayList<>(presets.values()))
                .add("uuid", uuid.toString())
                .build();
    }

    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return CompletableFuture.completedFuture(Optional.ofNullable(presets.get(name.toLowerCase(Locale.ROOT))));
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        presets.put(preset.name().toLowerCase(Locale.ROOT), preset);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return CompletableFuture.completedFuture(presets.remove(name.toLowerCase(Locale.ROOT)) != null);
    }

    @Override
    public CompletableFuture<Collection<Preset>> all() {
        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(presets.values()));
    }

    @Override
    public CompletableFuture<ContainerPagedAccess<Preset>> paged() {
        return CompletableFuture.completedFuture(new YamlContainerPagedAccess<>(this));
    }

    @Override
    public Set<String> names() {
        return presets.keySet();
    }

    @Override
    public CompletableFuture<Integer> size() {
        return CompletableFuture.completedFuture(presets.size());
    }

    @Override
    public @NotNull UUID owner() {
        return uuid;
    }

    public YamlPresetContainer updateOwner(UUID uuid) {
        this.uuid = uuid;
        return this;
    }
}
