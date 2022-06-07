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

/**
 * Represents a source where schematics can be drawn from.
 * <p>
 * Provides excludions
 */
public interface SchematicSource extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    String path();

    String prefix();

    List<String> excludedPath();

    /**
     * Checks wheather the path is excluded or not
     *
     * @param path path
     * @return true if excluded
     */
    boolean isExcluded(Path path);
}
