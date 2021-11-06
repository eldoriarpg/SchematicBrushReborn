/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
