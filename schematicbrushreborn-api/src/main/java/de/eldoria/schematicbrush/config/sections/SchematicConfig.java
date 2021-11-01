package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SerializableAs("sbrSchematicConfig")
public class SchematicConfig implements ConfigurationSerializable {
    private final List<SchematicSource> sources;
    private final String pathSeparator;
    private final boolean pathSourceAsPrefix;

    public SchematicConfig() {
        sources = new ArrayList<>();
        sources.add(new SchematicSource("SchematicBrushReborn/schematics", "sbr", new ArrayList<>()));
        sources.add(new SchematicSource("FastAsyncWorldEdit/schematics", "fawe", new ArrayList<>()));
        sources.add(new SchematicSource("WorldEdit/schematics", "we", new ArrayList<>()));
        pathSeparator = "/";
        pathSourceAsPrefix = false;
    }

    public SchematicConfig(List<SchematicSource> sources, String pathSeparator, boolean pathSourceAsPrefix) {
        this.sources = sources;
        this.pathSeparator = pathSeparator;
        this.pathSourceAsPrefix = pathSourceAsPrefix;
    }

    public SchematicConfig(Map<String, Object> objectMap) {
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

    public void addSource(SchematicSource source) {
        sources.add(source);
    }

    public List<SchematicSource> getSources() {
        return sources;
    }

    public String getPathSeparator() {
        return pathSeparator.substring(0, 1);
    }

    public boolean isPathSourceAsPrefix() {
        return pathSourceAsPrefix;
    }

    public Optional<SchematicSource> getSourceForPath(Path path) {
        return sources.stream().filter(source -> path.startsWith(source.getPath())).findFirst();
    }
}
