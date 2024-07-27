/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.modifier;

import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

import java.util.Objects;

/**
 * Represents a modifier.
 * <p>
 * A modifier is an extended {@link Nameable} which also has a description.
 */
public class BaseModifier extends Nameable {
    private final String description;
    private final String localizedName;
    private final boolean required;

    public BaseModifier(String name, String description, String localizedName, boolean required) {
        super(name);
        this.description = description;
        this.localizedName = Objects.requireNonNullElse(localizedName, name);
        this.required = required;
    }

    protected BaseModifier(String name, String description, boolean required) {
        this(name, description, null, required);
    }

    /**
     * Get a short explanation about this modifier.
     *
     * @return the description
     */
    public String description() {
        if (ILocalizer.isLocaleCode(description)) {
            return ILocalizer.escape(description);
        }
        return description;
    }

    /**
     * Defines whether this provider is required or not for a brush.
     * If the brush behaviour wouldn't change when the default value is applied a modifier is usually not considered required.
     *
     * @return true if the modifier is required.
     */
    public boolean required() {
        return required;
    }

    public String getLocalizedName() {
        return ILocalizer.escape(localizedName);
    }
}
