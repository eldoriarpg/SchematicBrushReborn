/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SchematicConfig extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    /**
     * Adds a new schematic source.
     * @param source source
     */
    void addSource(SchematicSource source);

    /**
     * A list of registered sources.
     *
     * @return list of sources
     */
    List<? extends SchematicSource> sources();

    /**
     * The path separator which should be used when showing pathes.
     * @return path separator
     */
    String pathSeparator();

    /**
     * Defines wheather the path should be prefixed or not
     *
     * @return true if the prefix should used as a suffix for the source path
     */
    boolean isPathSourceAsPrefix();

    /**
     * Gets the source for a path if present
     *
     * @param path path
     * @return optional holding a source
     */
    Optional<? extends SchematicSource> getSourceForPath(Path path);
}
