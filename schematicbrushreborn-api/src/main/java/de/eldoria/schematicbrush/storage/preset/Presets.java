/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.preset;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A schematic registry to manage {@link PresetContainer}
 */
public interface Presets {

    /**
     * Get presets of a player by name
     *
     * @param player player to add
     * @param name   name
     * @return preset with this name if exists
     * @deprecated Use {@link #playerContainer(UUID)} and {@link PresetContainer#get(String)} instead
     */
    @Deprecated(forRemoval = true)
    CompletableFuture<Optional<Preset>> getPreset(Player player, String name);

    /**
     * Get a global preset by name
     *
     * @param name name
     * @return preset with this name if exists
     * @deprecated Use {@link #globalContainer()} (UUID)} and {@link PresetContainer#get(String)} instead
     */
    @Deprecated(forRemoval = true)
    CompletableFuture<Optional<Preset>> getGlobalPreset(String name);

    /**
     * Add a player preset
     *
     * @param player player
     * @param preset preset
     * @return Future which completes after the preset got added
     * @deprecated Use {@link #playerContainer(UUID)} ()} (UUID)} and {@link PresetContainer#add(Preset)} instead
     */
    @Deprecated(forRemoval = true)
    CompletableFuture<Void> addPreset(Player player, Preset preset);

    /**
     * Add a global preset
     *
     * @param preset preset
     * @return Future which completes after the preset got added
     * @deprecated Use {@link #globalContainer()} (UUID)} and {@link PresetContainer#add(Preset)} instead
     */
    @Deprecated(forRemoval = true)
    CompletableFuture<Void> addPreset(Preset preset);

    /**
     * Remove a player preset
     *
     * @param player player
     * @param name   name
     * @return true if preset was removed
     * @deprecated Use {@link #playerContainer(Player)} ()} (UUID)} and {@link PresetContainer#remove(String)} instead
     */
    @Deprecated(forRemoval = true)
    CompletableFuture<Boolean> removePreset(Player player, String name);

    /**
     * Remove a global preset
     *
     * @param name name
     * @return true if preset was removed
     * @deprecated Use {@link #globalContainer()} (UUID)} and {@link PresetContainer#remove(String)} instead
     */
    @Deprecated(forRemoval = true)
    CompletableFuture<Boolean> removePreset(String name);

    /**
     * Get presets of a player
     *
     * @param player player
     * @return all presets of the player
     */
    default PresetContainer playerContainer(Player player) {
        return playerContainer(player.getUniqueId());
    }

    /**
     * Get presets of a player
     *
     * @param player player
     * @return all presets of the player
     */
    PresetContainer playerContainer(UUID player);

    /**
     * Get global presets
     *
     * @return all global presets
     */
    PresetContainer globalContainer();

    /**
     * Get all player presets
     *
     * @return all player presets in a map with the player uuid and associated presets
     */
    CompletableFuture<Map<UUID, ? extends PresetContainer>> getPlayerPresets();

    /**
     * Complete presets
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
