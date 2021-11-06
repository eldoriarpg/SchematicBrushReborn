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

import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Representing a schematic preset which holds multiple schematic sets.
 */
public interface Preset extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    /**
     * Name of the preset. This is unique for each container
     *
     * @return name
     */
    String name();

    /**
     * The preset as a info compnent used for medium details
     *
     * @param global if the preset is a global preset
     * @param canDelete when true a delete command should be included
     * @return component
     */
    String infoComponent(boolean global, boolean canDelete);

    /**
     * The detailed component with interactable buttons
     *
     * @param global indicates if the preset is a global preset
     * @return component
     */
    String detailComponent(boolean global);

    /**
     * The simple compnent used for tooltips
     *
     * @return compnent
     */
    String simpleComponent();

    /**
     * Description of the preset
     *
     * @return description
     */
    String description();

    /**
     * Set the description
     *
     * @param description the description
     */
    void description(String description);

    /**
     * Get all schematic set builders contained in the preset
     *
     * @return list of presets
     */
    List<SchematicSetBuilder> schematicSets();
}
