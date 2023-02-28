/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.config.ConfigurationImpl;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

public class YamlStorage implements Storage {
    private final ConfigurationImpl configuration;

    public YamlStorage(ConfigurationImpl configuration) {
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
        configuration.save();
    }
}
