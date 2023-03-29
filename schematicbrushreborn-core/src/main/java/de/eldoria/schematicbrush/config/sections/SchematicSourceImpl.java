/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@SerializableAs("sbrSchematicSource")
public class SchematicSourceImpl implements SchematicSource {
    private final String path;
    private final boolean relative;
    private final String prefix;
    private final List<String> excludedPath;

    public SchematicSourceImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        path = map.getValue("path");
        prefix = map.getValue("prefix");
        relative = map.getValueOrDefault("relative", true);
        excludedPath = map.getValue("excludedPath");
    }

    @JsonCreator
    public SchematicSourceImpl(@JsonProperty("path") String path,
                               @JsonProperty("prefix") String prefix,
                               @JsonProperty("relative") boolean relative,
                               @JsonProperty("excludedPath") List<String> excludedPath) {
        this.path = path;
        this.prefix = prefix;
        this.relative = relative;
        this.excludedPath = excludedPath;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public boolean isRelative() {
        return relative;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public List<String> excludedPath() {
        return excludedPath;
    }

    @Override
    public boolean isExcluded(Path path) {
        var internal = internalPath(path);

        for (var excluded : excludedPath) {
            if (excluded.equalsIgnoreCase(internal.toString())) return true;
            if (excluded.endsWith("*") && internal.startsWith(excluded)) return true;
        }
        return false;
    }

    @Override
    public boolean isSource(Path path) {
        if (isRelative()) {
            // Strip plugin directory
            var directory = path.subpath(1, path.getNameCount());
            return directory.startsWith(path());
        }
        return path.startsWith(path());
    }

    @Override
    public Path internalPath(Path path) {
        var internal = path;
        if (isRelative()) {
            // Strip relative plugin directory.
            internal = internal.subpath(1, internal.getNameCount());
        }
        // Strip base path from internal path
        if (!internal.equals(Paths.get(path()))) {
            return internal.subpath(Paths.get(path()).getNameCount(), internal.getNameCount());
        }
        return Paths.get("");
    }
}
