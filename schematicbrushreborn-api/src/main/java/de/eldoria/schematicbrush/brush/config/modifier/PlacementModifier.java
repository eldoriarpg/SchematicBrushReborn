/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
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
    public static final PlacementModifier PLACEMENT = of("Placement", "placementModifier.placement.name","placementModifier.placement.description", true);

    /**
     * IncludeAir modifier key
     */
    public static final PlacementModifier INCLUDE_AIR = of("IncludeAir", "placementModifier.includeAir.name","placementModifier.includeAir.description", true);

    /**
     * ReplaceAll modifier key
     */
    public static final PlacementModifier REPLACE_ALL = of("ReplaceAll", "placementModifier.replaceAll.name","placementModifier.replaceAll.description", true);

    /**
     * Offset modifier key
     */
    public static final PlacementModifier OFFSET = of("Offset", "placementModifier.offset.name","placementModifier.offset.description", false);

    /**
     * Filter modifier key
     */
    public static final PlacementModifier FILTER = of("Filter", "placementModifier.filter.name","placementModifier.filter.description", false);

    /**
     * Flip modifier key
     */
    public static final PlacementModifier FLIP = of("Flip", "placementModifier.flip.name","Flip a schematic", false);

    /**
     * Rotation modifier key
     */
    public static final PlacementModifier ROTATION = of("Rotation", "placementModifier.rotate.name","Rotate a schematic", false);


    private PlacementModifier(String name, String localeKey, String description, boolean required) {
        super(name, localeKey, description, required);
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
        return new PlacementModifier(name, name, description, required) {
        };
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name        name of the modifier
     * @param description the description of the modifier
     * @param required    true if this modifier is required to be set. This will enforce a default value for the modifier.
     * @return new PlacementModifier
     */
    public static PlacementModifier of(String name, String localeKey, String description, boolean required) {
        return new PlacementModifier(name, localeKey, description, required) {
        };
    }
}
