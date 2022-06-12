/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface GeneralConfig extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    Nameable storageType();
    boolean isCheckUpdates();

    boolean isPreviewDefault();

    boolean isShowNameDefault();

    MessageChannel<?> defaultNameChannel();

    int previewRefreshInterval();

    int maxRenderMs();

    int maxRenderSize();

    int renderDistance();

    int maxEffectiveRenderSize();
}
