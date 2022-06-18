/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

public class YamlStorage implements Storage {
    private final Presets yamlPresets;

    public YamlStorage(Presets yamlPresets) {
        this.yamlPresets = yamlPresets;
    }

    @Override
    public Presets presets() {
        return yamlPresets;
    }

    @Override
    public Brushes brushes() {
        //TODO:
        return null;
    }
}
