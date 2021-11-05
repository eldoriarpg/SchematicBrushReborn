package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SerializableAs("sbrSchematicSource")
public class SchematicSourceImpl implements SchematicSource {
    private final String path;
    private final String prefix;
    private final List<String> excludedPath;

    public SchematicSourceImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        path = map.getValue("path");
        prefix = map.getValue("prefix");
        excludedPath = map.getValue("excludedPath");
    }

    public SchematicSourceImpl(String path, String prefix, List<String> excludedPath) {
        this.path = path;
        this.prefix = prefix;
        this.excludedPath = excludedPath;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public List<String> getExcludedPath() {
        return excludedPath;
    }

    @Override
    public boolean isExcluded(Path path) {
        var split = path.toString().split("/");
        var internalPath = String.join("/", Arrays.copyOfRange(split, 1, split.length));
        for (var excluded : excludedPath) {
            if (excluded.equalsIgnoreCase(internalPath)) return true;
            if (excluded.endsWith("*") && internalPath.startsWith(excluded)) return true;
        }
        return false;
    }
}
