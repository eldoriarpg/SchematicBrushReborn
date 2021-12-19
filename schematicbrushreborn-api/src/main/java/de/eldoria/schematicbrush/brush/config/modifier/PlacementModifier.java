/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
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
    public static final PlacementModifier INCLUDE_AIR = of("IncludeAir", "Include air when placing. Will only have an effect when ReplaceAll is active.");

    /**
     * ReplaceAll modifier key
     */
    public static final PlacementModifier REPLACE_ALL = of("ReplaceAll", "Replace non air blocks");

    /**
     * Offset modifier key
     */
    public static final PlacementModifier OFFSET = of("Offset", "The schematic offset when placed.");

    /**
     * Filter modifier key
     */
    public static final PlacementModifier FILTER = of("Filter", "Remove blocks from the schematic.");

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
