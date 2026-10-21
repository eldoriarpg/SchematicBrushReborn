/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import java.nio.file.Path;
import java.util.List;

/**
 * Represents a source where schematics can be drawn from.
 * <p>
 * Provides excludions
 */
public interface SchematicSource {
    /**
     * Path of the schematic source.
     *
     * @return path as string
     */
    String path();

    /**
     * An ideally unique prefix for this source
     *
     * @return prefix
     */
    boolean isRelative();

    String prefix();

    /**
     * A list of relative pathes to the source which should be excluded
     *
     * @return list of paths
     */
    List<String> excludedPath();

    /**
     * Checks wheather the path is excluded or not
     *
     * @param path path
     * @return true if excluded
     */
    boolean isExcluded(Path path);

    /**
     * Checks if the path is part of this source.
     *
     * @param path path
     * @return true if path is in source
     */
    boolean isSource(Path path);

    /**
     * Get the internal path by stripping the path of the source.
     *
     * @param path path to strip
     * @return stripped path. Path may be empty.
     */
    Path internalPath(Path path);
}
