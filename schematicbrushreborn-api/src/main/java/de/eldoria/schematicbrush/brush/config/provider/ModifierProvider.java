/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Represents a modifier provider to provide {@link Mutator}.
 */
public abstract class ModifierProvider extends SettingProvider<Mutator<?>> {
    /**
     * Default constructor
     *
     * @param clazz class which is provided
     * @param name  name of provider
     * @deprecated Use {@link #ModifierProvider(Class, String, String, String)} and provide a localized name and description
     */
    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }

    /**
     * Default constructor
     *
     * @param clazz         which is returned by the provider
     * @param name          name. Must be unique inside the provider.
     * @param localizedName The property key to the name
     * @deprecated Use {@link #ModifierProvider(Class, String, String, String)} and provide a localized name and description
     */
    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName) {
        super(clazz, name, localizedName);
    }

    /**
     * Default constructor
     *
     * @param clazz         which is returned by the provider
     * @param name          name. Must be unique inside the provider.
     * @param localizedName The property key for the name
     * @param description   A description. Might be a string or a property key
     */
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description) {
        super(clazz, name, localizedName, description);
    }
}
