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
    public static final PlacementModifier PLACEMENT = of(
            "Placement",
            "components.modifier.type.placement.name",
            "components.modifier.type.placement.description",
            true);

    /**
     * IncludeAir modifier key
     */
    public static final PlacementModifier INCLUDE_AIR = of("IncludeAir",
            "components.modifier.type.includeAir.name",
            "components.modifier.type.includeAir.description", true);

    /**
     * ReplaceAll modifier key
     */
    public static final PlacementModifier REPLACE_ALL = of("ReplaceAll",
            "components.modifier.type.replaceAll.name",
            "components.modifier.type.replaceAll.description", true);

    /**
     * Offset modifier key
     */
    public static final PlacementModifier OFFSET = of("Offset",
            "components.modifier.type.offset.name",
            "components.modifier.type.offset.description", false);

    /**
     * Filter modifier key
     */
    public static final PlacementModifier FILTER = of("Filter",
            "components.modifier.type.filter.name",
            "components.modifier.type.filter.description", false);

    /**
     * Flip modifier key
     */
    public static final PlacementModifier FLIP = of("Flip",
            "components.modifier.type.flip.name",
            "components.modifier.type.flip.description", false);

    /**
     * Rotation modifier key
     */
    public static final PlacementModifier ROTATION = of("Rotation",
            "components.modifier.type.rotation.name",
            "components.modifier.type.rotation.description", false);


    private PlacementModifier(String name, String description, String localizedName, boolean required) {
        super(name, description, localizedName, required);
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name        name of the modifier
     * @param description the description of the modifier
     * @param required    true if this modifier is required to be set. This will enforce a default value for the modifier.
     * @return new PlacementModifier
     */
    public static PlacementModifier of(String name, String localizedName, String description, boolean required) {
        return new PlacementModifier(name, description, localizedName, required) {
        };
    }
}
