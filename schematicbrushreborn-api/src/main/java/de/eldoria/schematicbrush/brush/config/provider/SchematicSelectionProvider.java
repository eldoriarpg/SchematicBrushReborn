/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.brush.config.schematics.SchematicSelection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class SchematicSelectionProvider extends SettingProvider<SchematicSelection>{
    /**
     * Create a new settings provider
     *
     * @param clazz which is returned by the provider
     * @param name  name. Must be unique inside the provider.
     * @deprecated Use {@link #SchematicSelectionProvider(Class, String, String, String)} and provide a localized name and description
     */
    @SuppressWarnings("removal")
    public SchematicSelectionProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }

    public SchematicSelectionProvider(Class<? extends ConfigurationSerializable> clazz, String name, String localizedName, String description) {
        super(clazz, name, localizedName, description);
    }
}
