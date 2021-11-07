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

public interface SchematicSource extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    String getPath();

    String getPrefix();

    List<String> getExcludedPath();

    boolean isExcluded(Path path);
}
