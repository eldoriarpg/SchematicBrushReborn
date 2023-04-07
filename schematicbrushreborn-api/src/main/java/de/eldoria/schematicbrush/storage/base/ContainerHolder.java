/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.base;

import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Interface which represents a container holder.
 * <p>
 * A container holder always contains containers for any requested player.
 * <p>
 * A container holder always contains a global container.
 *
 * @param <V> Type which is contained in the container
 * @param <T> Type of the container
 */
public interface ContainerHolder<V, T extends Container<V>> {
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
     * Get container of a player
     *
     * @param player player
     * @return container of the player
     */
    T playerContainer(UUID player);

    /**
     * Get global container
     *
     * @return global container
     */
    T globalContainer();

    /**
     * Get all player containers
     *
     * @return all player containers in a map with the player uuid and associated presets
     */
    CompletableFuture<Map<UUID, ? extends T>> playerContainers();

    /**
     * Complete container values
     *
     * @param player player
     * @param arg    arguments to complete
     * @return list of possible values
     */
    default List<String> complete(Player player, String arg) {
        if (arg.startsWith("g:")) {
            return completeGlobal(arg.substring(2))
                    .stream()
                    .map(name -> "g:" + name)
                    .collect(Collectors.toList());
        }
        return completePlayer(player, arg);
    }

    /**
     * complete global container values
     *
     * @param arg name
     * @return list of matching values
     */
    default List<String> completeGlobal(String arg) {
        return TabCompleteUtil.complete(arg, globalContainer().names());
    }

    /**
     * Complete player container values
     *
     * @param player player
     * @param arg    name
     * @return list of matching values
     */
    default List<String> completePlayer(Player player, String arg) {
        var names = playerContainer(player).names();
        return TabCompleteUtil.complete(arg, names);
    }

    /**
     * Get the count of all existing presets
     *
     * @return preset count
     */
    CompletableFuture<Integer> count();

    /**
     * Method to merge a container into this container.
     * <p>
     * This will override entries if they already exist with the same name.
     * This will not remove already existing entries.
     *
     * @param container container to merge
     */
    default void migrate(ContainerHolder<V, T> container) {
        globalContainer().migrate(container.globalContainer());
        Map<UUID, ? extends T> map = container.playerContainers().join();
        for (Map.Entry<UUID, ? extends T> entry : map.entrySet()) {
            playerContainer(entry.getKey()).migrate(entry.getValue());
        }
    }
}
