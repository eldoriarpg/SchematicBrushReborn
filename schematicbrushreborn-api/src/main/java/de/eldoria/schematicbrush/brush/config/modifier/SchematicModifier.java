/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;

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
    public static final SchematicModifier ROTATION = of("Rotation", "Rotate a schematic", false);

    /**
     * Flip modifier key
     */
    public static final SchematicModifier FLIP = of("Flip", "Flip a schematic", false);

    /**
     * Offset modifier key
     */
    public static final SchematicModifier OFFSET = of("Offset", "The schematic offset when placed.", false);

    /**
     * Creates a new schematic modifier
     *
     * @param name        name. Defines the type of the modifier
     * @param description description of the modifier
     * @param required    true if this modifier is required to be set. This will enforce a default value for the modifier.
     */
    public SchematicModifier(String name, String description, boolean required) {
        super(name, description, required);
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name        name of the modifier
     * @param description description of the modifier
     * @param required    true if this modifier is required to be set. This will enforce a default value for the modifier.
     * @return new PlacementModifier
     */
    public static SchematicModifier of(String name, String description, boolean required) {
        return new SchematicModifier(name, description, required) {
        };
    }
}
