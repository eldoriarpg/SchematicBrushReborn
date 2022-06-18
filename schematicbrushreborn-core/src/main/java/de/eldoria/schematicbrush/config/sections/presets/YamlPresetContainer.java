/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.events.ConfigModifiedEvent;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
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

public class YamlPresetContainer implements PresetContainer, ConfigurationSerializable {
    private final Map<String, Preset> presets = new HashMap<>();

    public YamlPresetContainer(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Preset> presetList = map.getValue("presets");
        presetList.forEach(p -> presets.put(p.name(), p));
    }

    public YamlPresetContainer() {
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("presets", new ArrayList<>(presets.values()))
                .build();
    }

    /**
     * Get a preset by name
     *
     * @param name name of preset
     * @return optional containing the preset if found
     */
    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return CompletableFuture.completedFuture(Optional.ofNullable(presets.get(name.toLowerCase(Locale.ROOT))));
    }

    /**
     * Add a preset
     *
     * @param preset preset to add
     * @return
     */
    @Override
    public CompletableFuture<Void> add(Preset preset) {
        presets.put(preset.name(), preset);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Remove a preset by name
     *
     * @param name name of preset
     * @return true if the preset was removed
     */
    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return CompletableFuture.completedFuture(presets.remove(name.toLowerCase(Locale.ROOT)) != null);
    }

    /**
     * Get all presets in this container
     *
     * @return unmodifiable collection
     */
    @Override
    public CompletableFuture<Collection<Preset>> all() {
        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(presets.values()));
    }

    /**
     * Returns all names in this preset container
     *
     * @return set of names
     */
    @Override
    public Set<String> names() {
        return presets.keySet();
    }

    @Override
    public void close() throws IOException {
        Bukkit.getPluginManager().callEvent(new ConfigModifiedEvent());
    }
}
