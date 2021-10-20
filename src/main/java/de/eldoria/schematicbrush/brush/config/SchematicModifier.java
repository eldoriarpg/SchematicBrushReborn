package de.eldoria.schematicbrush.brush.config;

public abstract class SchematicModifier extends Nameable {
    public static final SchematicModifier ROTATION = of("rotation");
    public static final SchematicModifier FLIP = of("flip");
    public static final SchematicModifier WEIGHT = of("weight");

    public SchematicModifier(String name) {
        super(name);
    }

    public static SchematicModifier of(String name) {
        return new SchematicModifier(name) {
        };
    }
}
