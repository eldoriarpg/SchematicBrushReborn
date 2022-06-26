/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.brush;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("sbrBrush")
public class Brush implements ConfigurationSerializable {
    private String name;
    private BrushBuilderSnapshot snapshot;

    public Brush(String name, BrushBuilderSnapshot snapshot) {
        this.name = name;
        this.snapshot = snapshot;
    }

    @Deprecated
    public Brush() {
    }

    public Brush(String name, BrushBuilder snapshot) {
        this.name = name;
        this.snapshot = snapshot.snapshot();
    }

    public Brush(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        snapshot = map.getValue("snapshot");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("snapshot", snapshot)
                .build();
    }

    public String name() {
        return name;
    }

    public BrushBuilderSnapshot snapshot() {
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brush brush)) return false;

        if (!name.equals(brush.name)) return false;
        return snapshot.equals(brush.snapshot);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + snapshot.hashCode();
        return result;
    }
}
