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

package de.eldoria.schematicbrush.config;

import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistry;

/**
 * Plugin configuration
 */
@SuppressWarnings("unused")
public interface Configuration {
    void saveConfigs();

    void reloadConfigs();

    /**
     * The schematic config
     *
     * @return schematic config
     */
    SchematicConfig schematicConfig();

    /**
     * The general config
     *
     * @return general
     */
    GeneralConfig general();

    /**
     * The preset registry
     *
     * @return preset registry
     */
    PresetRegistry presets();
}
