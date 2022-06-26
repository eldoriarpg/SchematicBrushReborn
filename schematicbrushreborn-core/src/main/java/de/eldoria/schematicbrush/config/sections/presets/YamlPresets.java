/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import de.eldoria.schematicbrush.storage.preset.Presets;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlPresets implements Presets, ConfigurationSerializable {
    private final Map<UUID, YamlPresetContainer> playerPresets;
    private final YamlPresetContainer globalPresets;

    public YamlPresets(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        playerPresets = map.getMap("playerPresets", (key, v) -> UUID.fromString(key));
        globalPresets = map.getValueOrDefault("globalPresets", new YamlPresetContainer());
    }

    public YamlPresets() {
        playerPresets = new HashMap<>();
        globalPresets = new YamlPresetContainer();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("playerPresets", playerPresets, (key, v) -> key.toString())
                .add("globalPresets", globalPresets)
                .build();
    }

    @Override
    public PresetContainer playerContainer(Player player) {
        return playerContainer(player.getUniqueId());
    }

    @Override
    public PresetContainer playerContainer(UUID player) {
        return playerPresets.computeIfAbsent(player, key -> new YamlPresetContainer());
    }

    @Override
    public PresetContainer globalContainer() {
        return globalPresets;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends PresetContainer>> playerContainers() {
        return CompletableFuture.completedFuture(playerPresets);
    }

    @Override
    public CompletableFuture<Integer> count() {
        return CompletableFuture.completedFuture(globalPresets.all().join().size() + playerPresets.values().stream().mapToInt(container -> container.all().join().size()).sum());
    }
}
