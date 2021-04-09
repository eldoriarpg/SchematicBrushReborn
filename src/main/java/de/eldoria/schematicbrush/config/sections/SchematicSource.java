package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SerializableAs("sbrSchematicSource")
public class SchematicSource implements ConfigurationSerializable {
    private final String path;
    private final String prefix;
    private final List<String> excludedPath;

    public SchematicSource(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
        path = map.getValue("path");
        prefix = map.getValue("prefix");
        excludedPath = map.getValue("excludedPath");
    }

    public SchematicSource(String path, String prefix, List<String> excludedPath) {
        this.path = path;
        this.prefix = prefix;
        this.excludedPath = excludedPath;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    public String getPath() {
        return path;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> getExcludedPath() {
        return excludedPath;
    }

    public boolean isExcluded(Path path) {
        String[] split = path.toString().split("/");
        String internalPath = String.join("/", Arrays.copyOfRange(split, 1, split.length));
        for (String excluded : excludedPath) {
            if (excluded.equalsIgnoreCase(internalPath)) return true;
            if (excluded.endsWith("*") && internalPath.startsWith(excluded)) return true;
        }
        return false;
    }
}
