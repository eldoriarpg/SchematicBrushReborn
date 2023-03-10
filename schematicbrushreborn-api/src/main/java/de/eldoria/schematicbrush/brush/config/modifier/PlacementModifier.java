/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;

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
    public static final PlacementModifier PLACEMENT = of("Placement", "Define how the schematic should be placed on the position.", true);

    /**
     * IncludeAir modifier key
     */
    public static final PlacementModifier INCLUDE_AIR = of("IncludeAir", "Include air when placing. Will only have an effect when ReplaceAll is active.", true);

    /**
     * ReplaceAll modifier key
     */
    public static final PlacementModifier REPLACE_ALL = of("ReplaceAll", "Replace non air blocks", true);

    /**
     * Offset modifier key
     */
    public static final PlacementModifier OFFSET = of("Offset", "The schematic offset when placed.", false);

    /**
     * Filter modifier key
     */
    public static final PlacementModifier FILTER = of("Filter", "Remove blocks from the schematic.", false);

    /**
     * Flip modifier key
     */
    public static final PlacementModifier FLIP = of("Flip", "Flip a schematic", false);

    /**
     * Rotation modifier key
     */
    public static final PlacementModifier ROTATION = of("Rotation", "Rotate a schematic", false);


    private PlacementModifier(String name, String description, boolean required) {
        super(name, description, required);
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name        name of the modifier
     * @param description the description of the modifier
     * @param required    true if this modifier is required to be set. This will enforce a default value for the modifier.
     * @return new PlacementModifier
     */
    public static PlacementModifier of(String name, String description, boolean required) {
        return new PlacementModifier(name, description, required) {
        };
    }
}
