/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.storage.preset.Preset;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@SerializableAs("sbrPreset")
public class PresetImpl extends Preset {

    public PresetImpl(String name, List<SchematicSetBuilder> schematicSets) {
        this(name, "none", schematicSets);
    }

    public PresetImpl(String name, String description, List<SchematicSetBuilder> schematicSets) {
        super(name, schematicSets, description);
    }

    public static PresetImpl deserialize(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        String name = map.getValue("name");
        String description = map.getValue("description");
        if (description == null) {
            description = "none";
        }
        List<SchematicSetBuilder> schematicSets = map.getValue("sets");
        return new PresetImpl(name, description, schematicSets);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("description", description)
                .add("sets", schematicSets)
                .build();
    }
}
