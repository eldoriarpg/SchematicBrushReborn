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

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;

/**
 * Provider used to provide instance of classes implementing a {@link Selector}
 */
public abstract class SelectorProvider extends SettingProvider<Selector> {

    private final SchematicRegistry registry;

    /**
     * Creates a new provider instance
     *
     * @param clazz    class which is provided
     * @param name     name of selector
     * @param registry schematic registry. Can be retrieved via {@link SchematicBrushReborn#schematics()}
     */
    public SelectorProvider(Class<? extends Selector> clazz, String name, SchematicRegistry registry) {
        super(clazz, name);
        this.registry = registry;
    }

    /**
     * Returns the provided registry
     *
     * @return registry instance
     */
    public SchematicRegistry registry() {
        return registry;
    }
}
