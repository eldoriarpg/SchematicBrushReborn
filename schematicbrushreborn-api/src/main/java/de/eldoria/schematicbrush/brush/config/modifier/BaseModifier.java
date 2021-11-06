package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.config.util.Nameable;

public class BaseModifier extends Nameable {
    private final String description;

    protected BaseModifier(String name, String description) {
        super(name);
        this.description = description;
    }

    /**
     * Get a short explanation about this modifier.
     *
     * @return the description
     */
    public String description() {
        return description;
    }
}
