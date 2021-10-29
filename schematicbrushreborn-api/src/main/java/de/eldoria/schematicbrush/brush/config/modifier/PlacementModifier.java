package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.config.util.Nameable;

public abstract class PlacementModifier extends Nameable {
    public static final PlacementModifier PLACEMENT = of("placement");
    public static final PlacementModifier INCLUDE_AIR = of("includeair");
    public static final PlacementModifier REPLACE_ALL = of("replaceall");
    public static final PlacementModifier OFFSET = of("offset");


    public PlacementModifier(String name) {
        super(name);
    }

    public static PlacementModifier of(String name) {
        return new PlacementModifier(name) {
        };
    }
}
