/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Container which holds multiple presets.
 */
public interface PresetContainer extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    /**
     * Get a preset by name
     *
     * @param name name of preset
     * @return optional containing the preset if found
     */
    Optional<Preset> getPreset(String name);

    /**
     * Add a preset
     *
     * @param preset preset to add
     */
    void addPreset(Preset preset);

    /**
     * Remove a preset by name
     *
     * @param name name of preset
     * @return true if the preset was removed
     */
    boolean remove(String name);

    /**
     * Get all presets in this container
     *
     * @return unmodifiable collection
     */
    Collection<Preset> getPresets();

    /**
     * Returns all names in this preset container
     *
     * @return set of names
     */
    Set<String> names();
}
