/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A schematic registry to manage {@link PresetContainer}
 */
public interface PresetRegistry extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    /**
     * Get presets of a player by name
     *
     * @param player player to add
     * @param name   name
     * @return preset with this name if exists
     */
    Optional<Preset> getPreset(Player player, String name);

    /**
     * Add a player preset
     *
     * @param player player
     * @param preset preset
     */
    void addPreset(Player player, Preset preset);

    /**
     * Add a global preset
     *
     * @param preset preset
     */
    void addPreset(Preset preset);

    /**
     * Remove a player preset
     *
     * @param player player
     * @param name   name
     * @return true if preset was removed
     */
    boolean removePreset(Player player, String name);

    /**
     * Remove a global preset
     *
     * @param name name
     * @return true if preset was removed
     */
    boolean removePreset(String name);

    /**
     * Get presets of a player
     *
     * @param player player
     * @return all presets of the player
     */
    Collection<Preset> getPresets(Player player);

    /**
     * Get global presets
     *
     * @return all global presets
     */
    Collection<Preset> getPresets();

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
    int count();
}
