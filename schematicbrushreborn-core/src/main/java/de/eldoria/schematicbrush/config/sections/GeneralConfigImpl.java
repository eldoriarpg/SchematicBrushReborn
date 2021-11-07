/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "CanBeFinal"})
@SerializableAs("sbrGeneralSettings")
public class GeneralConfigImpl implements GeneralConfig {
    private boolean checkUpdates = true;
    private boolean previewDefault = true;
    private boolean showNameDefault = false;
    private int previewRefreshInterval = 1;
    private int maxRenderMs = 25;
    private int maxRenderSize = 2500;

    public GeneralConfigImpl() {
    }

    public GeneralConfigImpl(Map<String, Object> objectMap) {
        SerializationUtil.mapOnObject(objectMap, this);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    @Override
    public boolean isCheckUpdates() {
        return checkUpdates;
    }

    @Override
    public boolean isPreviewDefault() {
        return previewDefault;
    }

    @Override
    public boolean isShowNameDefault() {
        return showNameDefault;
    }

    @Override
    public int previewRefreshInterval() {
        return previewRefreshInterval;
    }

    @Override
    public int maxRenderMs() {
        return maxRenderMs;
    }

    @Override
    public int maxRenderSize() {
        return maxRenderSize;
    }
}
