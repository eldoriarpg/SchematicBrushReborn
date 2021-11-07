/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface GeneralConfig extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    boolean isCheckUpdates();

    boolean isPreviewDefault();

    boolean isShowNameDefault();

    int previewRefreshInterval();

    int maxRenderMs();

    int maxRenderSize();
}
