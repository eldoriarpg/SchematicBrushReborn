/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config;

import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

/**
 * Plugin configuration
 */
@SuppressWarnings("unused")
public interface Configuration {
    void saveConfigs();

    void reloadConfigs();

    /**
     * The schematic config
     *
     * @return schematic config
     */
    SchematicConfig schematicConfig();

    /**
     * The general config
     *
     * @return general
     */
    GeneralConfig general();

    /**
     * The preset storage
     *
     * @return preset storage
     */
    Presets presets();

    /**
     * The brush storage
     *
     * @return brush storage
     */
    Brushes brushes();
}
