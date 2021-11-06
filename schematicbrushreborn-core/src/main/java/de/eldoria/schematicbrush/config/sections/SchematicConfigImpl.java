/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.config.sections;

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
    private final List<SchematicSource> sources;
    private final String pathSeparator;
    private final boolean pathSourceAsPrefix;

    public SchematicConfigImpl() {
        sources = new ArrayList<>();
        sources.add(new SchematicSourceImpl("SchematicBrushReborn/schematics", "sbr", new ArrayList<>()));
        sources.add(new SchematicSourceImpl("FastAsyncWorldEdit/schematics", "fawe", new ArrayList<>()));
        sources.add(new SchematicSourceImpl("WorldEdit/schematics", "we", new ArrayList<>()));
        pathSeparator = "/";
        pathSourceAsPrefix = false;
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
    public List<SchematicSource> getSources() {
        return sources;
    }

    @Override
    public String getPathSeparator() {
        return pathSeparator.substring(0, 1);
    }

    @Override
    public boolean isPathSourceAsPrefix() {
        return pathSourceAsPrefix;
    }

    @Override
    public Optional<SchematicSource> getSourceForPath(Path path) {
        return sources.stream().filter(source -> path.startsWith(source.getPath())).findFirst();
    }
}
