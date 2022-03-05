/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.messages.MessageChannel;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface GeneralConfig extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    boolean isCheckUpdates();

    boolean isPreviewDefault();

    boolean isShowNameDefault();

    MessageChannel<?> defaultNameChannel();

    int previewRefreshInterval();

    int maxRenderMs();

    int maxRenderSize();

    int renderDistance();
}
