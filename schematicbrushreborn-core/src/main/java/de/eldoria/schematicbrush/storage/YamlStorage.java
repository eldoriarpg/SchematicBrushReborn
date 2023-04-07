/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.LegacyConfiguration;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

public class YamlStorage implements Storage {
    private final Configuration configuration;

    public YamlStorage(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Presets presets() {
        return configuration.presets();
    }

    @Override
    public Brushes brushes() {
        return configuration.brushes();
    }

    @Override
    public void save() {
        configuration.saveConfigs();
    }
}
