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

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PresetContainerImpl implements PresetContainer {
    private final Map<String, Preset> presets = new HashMap<>();

    public PresetContainerImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Preset> presetList = map.getValue("presets");
        presetList.forEach(p -> presets.put(p.name(), p));
    }

    public PresetContainerImpl() {
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("presets", new ArrayList<>(presets.values()))
                .build();
    }

    /**
     * Get a preset by name
     *
     * @param name name of preset
     * @return optional containing the preset if found
     */
    @Override
    public Optional<Preset> getPreset(String name) {
        return Optional.ofNullable(presets.get(name.toLowerCase(Locale.ROOT)));
    }

    /**
     * Add a preset
     *
     * @param preset preset to add
     */
    @Override
    public void addPreset(Preset preset) {
        presets.put(preset.name(), preset);
    }

    /**
     * Remove a preset by name
     *
     * @param name name of preset
     * @return true if the preset was removed
     */
    @Override
    public boolean remove(String name) {
        return presets.remove(name.toLowerCase(Locale.ROOT)) != null;
    }

    /**
     * Get all presets in this container
     *
     * @return unmodifiable collection
     */
    @Override
    public Collection<Preset> getPresets() {
        return Collections.unmodifiableCollection(presets.values());
    }

    /**
     * Returns all names in this preset container
     *
     * @return set of names
     */
    @Override
    public Set<String> names() {
        return presets.keySet();
    }
}
