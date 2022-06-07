/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
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

    void addSource(SchematicSource source);

    List<SchematicSource> sources();

    String pathSeparator();

    /**
     * Defines wheather the path should be prefixed or not
     * @return true if the prefix should used as a suffix for the source path
     */
    boolean isPathSourceAsPrefix();

    /**
     * Gets the source for a path if present
     * @param path path
     * @return optional holding a source
     */
    Optional<SchematicSource> getSourceForPath(Path path);
}
