package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

/**
 * Represents a placement modifier which is applied to a {@link de.eldoria.schematicbrush.brush.config.SchematicSet}.
 * <p>
 * A placement modifier must be added via {@link BrushSettingsRegistry#registerSchematicModifier(SchematicModifier, ModifierProvider)} (PlacementModifier, ModifierProvider)}.
 *
 * A Placement modifier is a key represented by a strin
 */
public abstract class SchematicModifier extends Nameable {
    /**
     * Roation modifier key
     */
    public static final SchematicModifier ROTATION = of("rotation");
    /**
     * Flip modifier key
     */
    public static final SchematicModifier FLIP = of("flip");

    public SchematicModifier(String name) {
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
