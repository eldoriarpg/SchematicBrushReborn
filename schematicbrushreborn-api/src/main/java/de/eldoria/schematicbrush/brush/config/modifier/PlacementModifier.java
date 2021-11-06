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

package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

/**
 * Represents a placement modifier which is applied to a {@link SchematicBrush}.
 * <p>
 * A placement modifier must be added via {@link BrushSettingsRegistry#registerPlacementModifier(PlacementModifier, ModifierProvider)}.
 * <p>
 * A Placement modifier is a key represented by a string
 */
public class PlacementModifier extends BaseModifier {
    /**
     * Placement modifier key
     */
    public static final PlacementModifier PLACEMENT = of("Placement", "Define how the schematic should be placed on the position.");

    /**
     * IncludeAir modifier key
     */
    public static final PlacementModifier INCLUDE_AIR = of("IncludeAir", "Include air when placing");

    /**
     * ReplaceAll modifier key
     */
    public static final PlacementModifier REPLACE_ALL = of("ReplaceAll", "Replace non air blocks");

    /**
     * Offset modifier key
     */
    public static final PlacementModifier OFFSET = of("Offset", "The schematic offset when placed.");

    private PlacementModifier(String name, String description) {
        super(name, description);
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name name of the modifier
     * @return new PlacementModifier
     */
    public static PlacementModifier of(String name, String description) {
        return new PlacementModifier(name, description) {
        };
    }
}
