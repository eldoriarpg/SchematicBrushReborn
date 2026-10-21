/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.brush.config.schematics.SchematicSelection;

public abstract class SchematicSelectionProvider extends SettingProvider<SchematicSelection> {
    /**
     * Create a new settings provider
     *
     * @param name          name. Must be unique inside the provider.
     * @param localizedName The property key for the name
     * @param description   A description. Might be a string or a property key
     */
    public SchematicSelectionProvider(String name, String localizedName, String description) {
        super(name, localizedName, description);
    }
}
