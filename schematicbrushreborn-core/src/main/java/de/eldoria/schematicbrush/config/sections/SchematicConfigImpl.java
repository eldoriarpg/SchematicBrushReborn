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
