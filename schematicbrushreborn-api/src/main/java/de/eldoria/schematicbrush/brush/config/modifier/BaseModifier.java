/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.schematicbrush.brush.config.util.Nameable;

/**
 * Represents a modifier.
 * <p>
 * A modifier is an extended {@link Nameable} which also has a description.
 */
public class BaseModifier extends Nameable {
    private final String localeKey;
    private final String description;
    private final boolean required;

    protected BaseModifier(String name, String description, boolean required) {
        this(name, name, description, required);
    }

    protected BaseModifier(String name, String localeKey, String description, boolean required) {
        super(name);
        this.localeKey = localeKey;
        this.description = description;
        this.required = required;
    }

    /**
     * Get a short explanation about this modifier.
     *
     * @return the description
     */
    public String description() {
        return description;
    }

    /**
     * Get the localization key of the modifier
     *
     * @return name key
     */
    public String localeKey() {
        return localeKey;
    }

    /**
     * Defines wheather this provider is required or not for a brush.
     * If the brush behaviour wouldn't change when the default value is applied a modifier is usually not considered required.
     *
     * @return true if the modifier is required.
     */
    public boolean required() {
        return required;
    }
}
