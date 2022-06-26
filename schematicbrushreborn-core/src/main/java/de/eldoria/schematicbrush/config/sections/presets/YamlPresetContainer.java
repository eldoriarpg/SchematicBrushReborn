/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

@SerializableAs("sbrYamlPresetContainer")
public class YamlPresetContainer implements PresetContainer, ConfigurationSerializable {
    private final Map<String, Preset> presets;

    public YamlPresetContainer(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Preset> presetList = map.getValueOrDefault("presets", Collections.emptyList());
        presets = new HashMap<>();
        presetList.forEach(p -> presets.put(p.name(), p));
    }

    public YamlPresetContainer() {
        presets = new HashMap<>();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("presets", new ArrayList<>(presets.values()))
                .build();
    }

    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return CompletableFuture.completedFuture(Optional.ofNullable(presets.get(name.toLowerCase(Locale.ROOT))));
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        presets.put(preset.name(), preset);
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
    public Set<String> names() {
        return presets.keySet();
    }

    @Override
    public void close() throws IOException {
    }
}
