/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "CanBeFinal"})
@SerializableAs("sbrGeneralSettings")
public class GeneralConfigImpl implements GeneralConfig {
    private String storageType = StorageRegistry.YAML.name();
    private boolean checkUpdates = true;
    private boolean previewDefault = true;
    private boolean showNameDefault = false;
    private MessageChannel defaultNameChannel = MessageChannel.ACTION_BAR;
    private int previewRefreshInterval = 1;
    private int maxRenderMs = 25;
    private int maxRenderSize = 2500;
    private int maxEffectiveRenderSize = maxRenderSize;
    private int renderDistance = 100;

    public GeneralConfigImpl() {
    }

    public GeneralConfigImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        checkUpdates = map.getValueOrDefault("checkUpdates", true);
        previewDefault = map.getValueOrDefault("previewDefault", true);
        showNameDefault = map.getValueOrDefault("showNameDefault", false);
        defaultNameChannel = map.getValueOrDefault("defaultNameChannel", MessageChannel.CHAT, MessageChannel.class);
        previewRefreshInterval = map.getValueOrDefault("previewRefreshInterval", 1);
        maxRenderMs = map.getValueOrDefault("maxRenderMs", 25);
        maxRenderSize = map.getValueOrDefault("maxRenderSize", 2500);
        maxEffectiveRenderSize = map.getValueOrDefault("maxEffectiveRenderSize", maxRenderSize);
        renderDistance = map.getValueOrDefault("renderDistance", 100);
        storageType = map.getValueOrDefault("storageType", StorageRegistry.YAML.name());
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
                .add("maxEffectiveRenderSize", maxEffectiveRenderSize)
                .add("renderDistance", renderDistance)
                .add("storageType", storageType)
                .build();
    }

    @Override
    public Nameable storageType() {
        return Nameable.of(storageType);
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
    public MessageChannel defaultNameChannel() {
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
    public double renderDistanceSquared() {
        return Math.pow(renderDistance(), 2);
    }

    @Override
    public int maxEffectiveRenderSize() {
        return maxEffectiveRenderSize;
    }

}
