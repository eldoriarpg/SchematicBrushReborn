/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 */

package de.eldoria.schematicbrush.config;

import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistry;

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
     * The preset registry
     *
     * @return preset registry
     */
    PresetRegistry presets();
}
