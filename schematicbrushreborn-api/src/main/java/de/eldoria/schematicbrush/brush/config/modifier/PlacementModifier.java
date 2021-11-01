package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

/**
 * Represents a placement modifier which is applied to a {@link SchematicBrush}.
 * <p>
 * A placement modifier must be added via {@link BrushSettingsRegistry#registerPlacementModifier(PlacementModifier, ModifierProvider)}.
 *
 * A Placement modifier is a key represented by a string
 */
public class PlacementModifier extends Nameable {
    /**
     * Placement modifier key
     */
    public static final PlacementModifier PLACEMENT = of("placement");
    /**
     * IncludeAir modifier key
     */
    public static final PlacementModifier INCLUDE_AIR = of("includeair");
    /**
     * ReplaceAll modifier key
     */
    public static final PlacementModifier REPLACE_ALL = of("replaceall");
    /**
     * Offset modifier key
     */
    public static final PlacementModifier OFFSET = of("offset");


    private PlacementModifier(String name) {
        super(name);
    }

    /**
     * Creates a new PlacementModifier.
     *
     * @param name name of the modifier
     * @return new PlacementModifier
     */
    public static PlacementModifier of(String name) {
        return new PlacementModifier(name) {
        };
    }
}
