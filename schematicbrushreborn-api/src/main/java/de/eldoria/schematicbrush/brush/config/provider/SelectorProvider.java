/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
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
     * @param name          name of selector
     * @param localizedName The property key for the name
     * @param description   A description. Might be a string or a property key
     * @param registry      schematic registry. Can be retrieved via {@link SchematicBrushReborn#schematics()}
     */
    public SelectorProvider(String name, String localizedName, String description, SchematicRegistry registry) {
        super(name, localizedName, description);
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
