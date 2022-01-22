/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PresetRegistryImpl implements PresetRegistry {

    private Map<UUID, PresetContainer> playerPresets = new HashMap<>();
    private PresetContainer globalPresets = new PresetContainerImpl();

    public PresetRegistryImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        playerPresets = map.getMap("playerPresets", (key, v) -> UUID.fromString(key));
        globalPresets = map.getValueOrDefault("globalPresets", new PresetContainerImpl());
    }

    public PresetRegistryImpl() {
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .addMap("playerPresets", playerPresets, (key, v) -> key.toString())
                .add("globalPresets", globalPresets)
                .build();
    }

    /**
     * Get a preset container of a player
     *
     * @param player player
     * @return preset container if exists
     */
    private Optional<PresetContainer> getPlayerPresets(Player player) {
        return Optional.ofNullable(playerPresets.get(player.getUniqueId()));
    }

    private PresetContainer getOrCreatePlayerPresets(Player player) {
        return playerPresets.computeIfAbsent(player.getUniqueId(), key -> new PresetContainerImpl());
    }

    /**
     * Get presets of a player by name
     *
     * @param player player to add
     * @param name   name
     * @return preset with this name if exists
     */
    @Override
    public Optional<Preset> getPreset(Player player, String name) {
        if (name.startsWith("g:")) {
            return globalPresets.getPreset(name.substring(2));
        }
        return getPlayerPresets(player).flatMap(p -> p.getPreset(name));
    }

    /**
     * Get a global preset by name
     *
     * @param name   name
     * @return preset with this name if exists
     */
    @Override
    public Optional<Preset> getGlobalPreset(String name) {
            return globalPresets.getPreset(name);
    }

    /**
     * Add a player preset
     *
     * @param player player
     * @param preset preset
     */
    @Override
    public void addPreset(Player player, Preset preset) {
        getOrCreatePlayerPresets(player).addPreset(preset);
    }

    /**
     * Add a global preset
     *
     * @param preset preset
     */
    @Override
    public void addPreset(Preset preset) {
        globalPresets.addPreset(preset);
    }

    /**
     * Remove a player preset
     *
     * @param player player
     * @param name   name
     * @return true if preset was removed
     */
    @Override
    public boolean removePreset(Player player, String name) {
        return getPlayerPresets(player).map(p -> p.remove(name)).orElse(false);
    }

    /**
     * Remove a global preset
     *
     * @param name name
     * @return true if preset was removed
     */
    @Override
    public boolean removePreset(String name) {
        return globalPresets.remove(name);
    }

    /**
     * Get presets of a player
     *
     * @param player player
     * @return all presets of the player
     */
    @Override
    public Collection<Preset> getPresets(Player player) {
        return getPlayerPresets(player).map(PresetContainer::getPresets).orElse(Collections.emptyList());
    }

    /**
     * Get global presets
     *
     * @return all global presets
     */
    @Override
    public Collection<Preset> getPresets() {
        return globalPresets.getPresets();
    }

    /**
     * Complete presets
     *
     * @param player player
     * @param arg    arguments to complete
     * @return list of possible values
     */
    @Override
    public List<String> complete(Player player, String arg) {
        if (arg.startsWith("g:")) {
            return TabCompleteUtil.complete(arg.substring(2), globalPresets.names())
                    .stream()
                    .map(name -> "g:" + name)
                    .collect(Collectors.toList());
        }
        var names = getPlayerPresets(player).map(PresetContainer::names).orElse(Collections.emptySet());
        return TabCompleteUtil.complete(arg, names);
    }

    @Override
    public int count() {
        return globalPresets.getPresets().size() + playerPresets.values().stream().mapToInt(container -> container.getPresets().size()).sum();
    }
}
