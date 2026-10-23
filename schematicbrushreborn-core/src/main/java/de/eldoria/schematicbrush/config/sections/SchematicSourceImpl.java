/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class SchematicSourceImpl implements SchematicSource {
    private final String path;
    private final boolean relative;
    private final String prefix;
    private final List<String> excludedPath;

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

    /**
     * Checks whether the directory is excluded. An entry ending with {@code *} excludes every directory whose path
     * starts with the text before it, so {@code old*} excludes {@code old} and everything below it, while
     * {@code old/*} excludes only the directories below {@code old}. Matching ignores case.
     *
     * @param path path
     * @return true if excluded
     */
    @Override
    public boolean isExcluded(Path path) {
        var internal = normalize(internalPath(path).toString());

        for (var excluded : excludedPath) {
            var entry = normalize(excluded);
            if (entry.endsWith("*")) {
                if (internal.startsWith(entry.substring(0, entry.length() - 1))) return true;
            } else if (entry.equals(internal)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String path) {
        return path.replace("\\", "/").toLowerCase(Locale.ROOT);
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
