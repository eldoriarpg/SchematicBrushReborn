/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.brush;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.storage.preset.Preset;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Class used for serialization and saving of a {@link BrushBuilderSnapshot}/
 */
@SerializableAs("sbrBrush")
public class Brush implements ConfigurationSerializable {
    private final String name;

    protected String description;

    private final BrushBuilderSnapshot snapshot;

    /**
     * Constructs a new brush with the given snapshot.
     *
     * @param name     name of brush
     * @param snapshot snapshot of brush
     */
    public Brush(String name, String description, BrushBuilderSnapshot snapshot) {
        this.name = name;
        this.description = description;
        this.snapshot = snapshot;
    }

    /**
     * Constructs a new brush with the given brush.
     *
     * @param name    name of brush
     * @param builder brush builder
     */
    public Brush(String name, BrushBuilder builder) {
        this(name, "none", builder.snapshot());
    }

    /**
     * Constructor required by {@link ConfigurationSerializable} in order to deserialize the object.
     */
    @SuppressWarnings("unused")
    public static Brush deserialize(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        String name = map.getValue("name");
        String description = map.getValue("description");
        if (description == null) {
            description = "none";
        }
        BrushBuilderSnapshot snapshot = map.getValue("snapshot");
        return new Brush(name, description, snapshot);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("snapshot", snapshot)
                .build();
    }

    /**
     * Name of brush
     *
     * @return name
     */
    public String name() {
        return name;
    }

    /**
     * Snapshot of the brush
     *
     * @return snapshot
     */
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
        var result = name.hashCode();
        result = 31 * result + snapshot.hashCode();
        return result;
    }

    public void description(String description) {
        this.description = description;
    }
}
