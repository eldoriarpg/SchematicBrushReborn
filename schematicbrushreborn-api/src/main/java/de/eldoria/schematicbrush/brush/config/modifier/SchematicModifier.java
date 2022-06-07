/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

/**
 * Represents a placement modifier which is applied to a {@link SchematicSet}.
 * <p>
 * A placement modifier must be added via {@link BrushSettingsRegistry#registerSchematicModifier(SchematicModifier, ModifierProvider)} (PlacementModifier, ModifierProvider)}.
 * <p>
 * A Placement modifier is a key represented by a string
 */
public class SchematicModifier extends BaseModifier {
    /**
     * Rotation modifier key
     */
    public static final SchematicModifier ROTATION = of("Rotation", "Rotate a schematic");

    /**
     * Flip modifier key
     */
    public static final SchematicModifier FLIP = of("Flip", "Flip a schematic");

    /**
     * Offset modifier key
     */
    public static final SchematicModifier OFFSET = of("Offset", "The schematic offset when placed.");

    /**
     * Creates a new schematic modifier
     * @param name name. Defines the type of the modifier
     * @param description description of the modifier
     */
    public SchematicModifier(String name, String description) {
        super(name, description);
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name name of the modifier
     * @param description description of the modifier
     * @return new PlacementModifier
     */
    public static SchematicModifier of(String name, String description) {
        return new SchematicModifier(name, description) {
        };
    }
}
