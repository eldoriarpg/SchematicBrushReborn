/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.brushes;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.storage.base.Container;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SerializableAs("sbrYamlBrushes")
public class YamlBrushes implements Brushes, ConfigurationSerializable {
    private final Map<UUID, YamlBrushContainer> playerBrushes;
    private final YamlBrushContainer globalBrushes;

    public YamlBrushes(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        playerBrushes = map.getMap("playerBrushes", (key, v) -> UUID.fromString(key));
        globalBrushes = map.getValueOrDefault("globalBrushes", new YamlBrushContainer(Container.GLOBAL));
    }

    @JsonCreator
    public YamlBrushes(@JsonProperty("playerBrushes") Map<UUID, YamlBrushContainer> playerBrushes,
                       @JsonProperty("globalBrushes") YamlBrushContainer globalBrushes) {
        this.playerBrushes = playerBrushes == null ? new HashMap<>() : playerBrushes;
        this.globalBrushes = globalBrushes == null ? new YamlBrushContainer(Container.GLOBAL) : globalBrushes;
    }

    public YamlBrushes() {
        playerBrushes = new HashMap<>();
        globalBrushes = new YamlBrushContainer(Container.GLOBAL);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("playerBrushes", playerBrushes, (key, v) -> key.toString())
                .add("globalBrushes", globalBrushes)
                .build();
    }

    @Override
    public BrushContainer playerContainer(Player player) {
        return playerContainer(player.getUniqueId());
    }

    @Override
    public BrushContainer playerContainer(UUID player) {
        return playerBrushes.computeIfAbsent(player, key -> new YamlBrushContainer(player)).updateOwner(player);
    }

    @Override
    public BrushContainer globalContainer() {
        return globalBrushes;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends BrushContainer>> playerContainers() {
        return CompletableFuture.completedFuture(playerBrushes);
    }

    @Override
    public CompletableFuture<Integer> count() {
        return CompletableFuture.completedFuture(globalBrushes.all().join().size() + playerBrushes.values().stream().mapToInt(container -> container.all().join().size()).sum());
    }
}
