/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import tools.jackson.databind.annotation.JsonDeserialize;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
