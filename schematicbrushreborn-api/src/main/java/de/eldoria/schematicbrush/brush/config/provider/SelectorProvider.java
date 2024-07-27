/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Provider used to provide instance of classes implementing a {@link Selector}
 */
public abstract class SelectorProvider extends SettingProvider<Selector> {

    private final SchematicRegistry registry;

    /**
     * Creates a new provider instance
     *
     * @param clazz    class which is provided
     * @param name     name of selector
     * @param registry schematic registry. Can be retrieved via {@link SchematicBrushReborn#schematics()}
     * @deprecated Use {@link #SelectorProvider(Class, String, String, String, SchematicRegistry)} and provide a localized name and description
     */
    @SuppressWarnings("removal")
    public SelectorProvider(Class<? extends Selector> clazz, String name, SchematicRegistry registry) {
        super(clazz, name);
        this.registry = registry;
    }


    public SelectorProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description, SchematicRegistry registry) {
        super(clazz, name, localizedName, description);
        this.registry = registry;
    }

    /**
     * Returns the provided registry
     *
     * @return registry instance
     */
    public SchematicRegistry registry() {
        return registry;
    }
}
