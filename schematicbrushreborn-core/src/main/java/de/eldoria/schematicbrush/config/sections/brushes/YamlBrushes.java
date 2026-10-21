/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.brushes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.storage.base.Container;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlBrushes implements Brushes {
    private final Map<UUID, YamlBrushContainer> playerBrushes;
    private final YamlBrushContainer globalBrushes;

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
