/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.brush;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Brush implements ConfigurationSerializable {
    private final String name;
    private final BrushBuilderSnapshot snapshot;

    public Brush(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        snapshot = map.getValue("snapshot");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    public String name() {
        return name;
    }

    public BrushBuilderSnapshot snapshot() {
        return snapshot;
    }
}
