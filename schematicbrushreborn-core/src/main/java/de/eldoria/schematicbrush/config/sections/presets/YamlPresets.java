/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import de.eldoria.schematicbrush.storage.preset.Presets;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class YamlPresets implements Presets, ConfigurationSerializable {
    private Map<UUID, YamlPresetContainer> playerPresets = new HashMap<>();
    private YamlPresetContainer globalPresets = new YamlPresetContainer();

    public YamlPresets(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        playerPresets = map.getMap("playerPresets", (key, v) -> UUID.fromString(key));
        globalPresets = map.getValueOrDefault("globalPresets", new YamlPresetContainer());
    }

    public YamlPresets() {
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("playerPresets", playerPresets, (key, v) -> key.toString())
                .add("globalPresets", globalPresets)
                .build();
    }

    private YamlPresetContainer getPlayerPresets(UUID player) {
        return playerPresets.computeIfAbsent(player, k -> new YamlPresetContainer());
    }

    private YamlPresetContainer getPlayerPresets(Player player) {
        return getPlayerPresets(player.getUniqueId());
    }

    private YamlPresetContainer getOrCreatePlayerPresets(Player player) {
        return playerPresets.computeIfAbsent(player.getUniqueId(), key -> new YamlPresetContainer());
    }

    @Override
    public PresetContainer playerContainer(Player player) {
        return getPlayerPresets(player);
    }

    @Override
    public PresetContainer playerContainer(UUID player) {
        return getPlayerPresets(player);
    }

    @Override
    public PresetContainer globalContainer() {
        return globalPresets;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends PresetContainer>> getPlayerPresets() {
        return CompletableFuture.completedFuture(playerPresets);
    }

    @Override
    public final List<String> complete(Player player, String arg) {
        if (arg.startsWith("g:")) {
            return completeGlobal(arg.substring(2))
                    .stream()
                    .map(name -> "g:" + name)
                    .collect(Collectors.toList());
        }
        return completePlayer(player, arg);
    }

    public List<String> completeGlobal(String arg) {
        return TabCompleteUtil.complete(arg, globalPresets.names());
    }

    public List<String> completePlayer(Player player, String arg) {
        var names = getPlayerPresets(player).names();
        return TabCompleteUtil.complete(arg, names);
    }

    @Override
    public CompletableFuture<Integer> count() {
        return CompletableFuture.completedFuture(globalPresets.getPresets().join().size() + playerPresets.values().stream().mapToInt(container -> container.getPresets().join().size()).sum());
    }
}
