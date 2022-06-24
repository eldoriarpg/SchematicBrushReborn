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
    private final Brushes yamlBrushes;

    public YamlStorage(Presets yamlPresets, Brushes yamlBrushes) {
        this.yamlPresets = yamlPresets;
        this.yamlBrushes = yamlBrushes;
    }

    @Override
    public Presets presets() {
        return yamlPresets;
    }

    @Override
    public Brushes brushes() {
        return yamlBrushes;
    }
}
