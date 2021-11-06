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

package de.eldoria.schematicbrush.brush.config.provider;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Represents a modifier provider to provide {@link Mutator}.
 */
public abstract class ModifierProvider extends SettingProvider<Mutator<?>> {
    /**
     * Default constructor
     *
     * @param clazz class which is provided
     * @param name  name of provider
     */
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }
}
