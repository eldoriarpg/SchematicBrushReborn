/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
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
     * @param localeKey the locale key of the name
     */
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localeKey) {
        super(clazz, name, localeKey);
    }
    /**
     * Default constructor
     *
     * @param clazz class which is provided
     * @param name  name of provider
     */
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name, name);
    }
}
