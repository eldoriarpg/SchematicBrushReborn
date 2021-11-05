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
public class SchematicModifier extends Nameable {
    /**
     * Rotation modifier key
     */
    public static final SchematicModifier ROTATION = of("Rotation");

    /**
     * Flip modifier key
     */
    public static final SchematicModifier FLIP = of("Flip");

    private SchematicModifier(String name) {
        super(name);
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name name of the modifier
     * @return new PlacementModifier
     */
    public static SchematicModifier of(String name) {
        return new SchematicModifier(name) {
        };
    }
}
