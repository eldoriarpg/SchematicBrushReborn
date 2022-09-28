/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;

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
     */
    public SelectorProvider(Class<? extends Selector> clazz, String name, String localeKey, SchematicRegistry registry) {
        super(clazz, name, localeKey);
        this.registry = registry;
    }

    /**
     * Creates a new provider instance
     *
     * @param clazz    class which is provided
     * @param name     name of selector
     * @param registry schematic registry. Can be retrieved via {@link SchematicBrushReborn#schematics()}
     */
    public SelectorProvider(Class<? extends Selector> clazz, String name, SchematicRegistry registry) {
        super(clazz, name, name);
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
