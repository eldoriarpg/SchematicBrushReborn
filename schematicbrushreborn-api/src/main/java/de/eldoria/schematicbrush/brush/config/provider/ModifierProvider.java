/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;


/**
 * Represents a modifier provider to provide {@link Mutator}.
 */
public abstract class ModifierProvider extends SettingProvider<Mutator<?>> {
    /**
     * Default constructor
     *
     * @param name          name. Must be unique inside the provider.
     * @param localizedName The property key for the name
     * @param description   A description. Might be a string or a property key
     */
    public ModifierProvider(String name, String localizedName, String description) {
        super(name, localizedName, description);
    }
}
