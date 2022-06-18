/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.base;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ContainerHolder<T> {
    /**
     * Get container of a player
     *
     * @param player player
     * @return container of the player
     */
    default T playerContainer(Player player) {
        return playerContainer(player.getUniqueId());
    }

    /**
     * Returns the global or player container based on the name provided
     *
     * @param player player
     * @param name   preset name
     * @return container, which holds the player or global presets
     */
    default T containerByName(Player player, String name) {
        return name.startsWith("g:") ? globalContainer() : playerContainer(player);
    }

    /**
     * Get presets of a player
     *
     * @param player player
     * @return container of the player
     */
    T playerContainer(UUID player);

    /**
     * Get global presets
     *
     * @return all global presets
     */
    T globalContainer();

    /**
     * Get all player presets
     *
     * @return all player presets in a map with the player uuid and associated presets
     */
    CompletableFuture<Map<UUID, ? extends T>> getPlayerPresets();

    /**
     * Complete container values
     *
     * @param player player
     * @param arg    arguments to complete
     * @return list of possible values
     */
    List<String> complete(Player player, String arg);

    /**
     * Get the count of all existing presets
     *
     * @return preset count
     */
    CompletableFuture<Integer> count();
}
