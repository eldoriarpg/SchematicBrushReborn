/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.messages.MessageChannel;
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
    private MessageChannel<?> defaultNameChannel = MessageChannel.ACTION_BAR;
    private int previewRefreshInterval = 1;
    private int maxRenderMs = 25;
    private int maxRenderSize = 2500;
    private int maxeffectiveRenderSize = maxRenderSize;
    private int renderDistance = 100;

    public GeneralConfigImpl() {
    }

    public GeneralConfigImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        checkUpdates = map.getValueOrDefault("checkUpdates", true);
        previewDefault = map.getValueOrDefault("previewDefault", true);
        showNameDefault = map.getValueOrDefault("showNameDefault", false);
        defaultNameChannel = map.getValueOrDefault("defaultNameChannel", MessageChannel.CHAT, MessageChannel::getChannelByNameOrDefault);
        previewRefreshInterval = map.getValueOrDefault("previewRefreshInterval", 1);
        maxRenderMs = map.getValueOrDefault("maxRenderMs", 25);
        maxRenderSize = map.getValueOrDefault("maxRenderSize", 2500);
        maxeffectiveRenderSize = map.getValueOrDefault("maxeffectiveRenderSize", maxRenderSize);
        renderDistance = map.getValueOrDefault("renderDistance", 100);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("checkUpdates", checkUpdates)
                .add("previewDefault", previewDefault)
                .add("showNameDefault", showNameDefault)
                .add("defaultNameChannel", defaultNameChannel.name())
                .add("previewRefreshInterval", previewRefreshInterval)
                .add("maxRenderMs", maxRenderMs)
                .add("maxRenderSize", maxRenderSize)
                .add("renderDistance", renderDistance)
                .build();
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
    public MessageChannel<?> defaultNameChannel() {
        return defaultNameChannel;
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

    @Override
    public int renderDistance() {
        return renderDistance;
    }

    @Override
    public int maxeffectiveRenderSize() {
        return maxeffectiveRenderSize;
    }
}
