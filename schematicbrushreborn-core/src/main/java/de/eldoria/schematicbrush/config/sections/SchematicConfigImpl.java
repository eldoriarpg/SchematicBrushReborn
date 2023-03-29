/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SerializableAs("sbrSchematicConfig")
public class SchematicConfigImpl implements SchematicConfig {
    @JsonDeserialize(contentAs = SchematicSourceImpl.class)
    private List<SchematicSource> sources = new ArrayList<>(List.of(
            new SchematicSourceImpl("SchematicBrushReborn/schematics", "sbr", true, new ArrayList<>()),
            new SchematicSourceImpl("FastAsyncWorldEdit/schematics", "fawe", true, new ArrayList<>()),
            new SchematicSourceImpl("WorldEdit/schematics", "we", true, new ArrayList<>())
    ));
    private String pathSeparator = "/";
    private boolean pathSourceAsPrefix = false;

    public SchematicConfigImpl() {
    }

    public SchematicConfigImpl(List<SchematicSource> sources, String pathSeparator, boolean pathSourceAsPrefix) {
        this.sources = sources;
        this.pathSeparator = pathSeparator;
        this.pathSourceAsPrefix = pathSourceAsPrefix;
    }

    public SchematicConfigImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        sources = map.getValue("sources");
        pathSeparator = map.getValue("pathSeparator");
        pathSourceAsPrefix = map.getValue("pathSourceAsPrefix");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("sources", sources)
                .add("pathSeparator", pathSeparator)
                .add("pathSourceAsPrefix", pathSourceAsPrefix)
                .build();
    }

    @Override
    public void addSource(SchematicSource source) {
        sources.add(source);
    }

    @Override
    public List<? extends SchematicSource> sources() {
        return sources;
    }

    @Override
    public String pathSeparator() {
        return pathSeparator.substring(0, 1);
    }

    @Override
    public boolean isPathSourceAsPrefix() {
        return pathSourceAsPrefix;
    }

    @Override
    public Optional<? extends SchematicSource> getSourceForPath(Path path) {
        return sources.stream().filter(source -> source.isSource(path)).findFirst();
    }
}
