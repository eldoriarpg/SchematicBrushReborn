/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.sections;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.storage.StorageRegistry;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "CanBeFinal"})
public class GeneralConfigImpl implements GeneralConfig {
    private String storageType = StorageRegistry.YAML.name();
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean checkUpdates = true;
    private boolean previewDefault = true;
    private boolean showNameDefault = false;
    private MessageChannel defaultNameChannel = MessageChannel.ACTION_BAR;
    private int previewRefreshInterval = 1;
    private int maxRenderMs = 25;
    private int maxRenderSize = 2500;
    private int maxEffectiveRenderSize = maxRenderSize;
    private int renderDistance = 100;
    private int pasteBreak = 20;
    private int previewResync = 10;
    private String language = "en_US";
    private EditorType editor = EditorType.DIALOG;

    public GeneralConfigImpl() {
    }
    @Override
    public Nameable storageType() {
        return Nameable.of(storageType);
    }

    /**
     * The update switch of configs from before the update settings, read once to start them.
     *
     * @return whether update checks were on
     */
    boolean checkUpdates() {
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

    @Override
    public int pasteBreak() {
        return pasteBreak;
    }

    @Override
    public int previewResync() {
        return previewResync;
    }

    @Override
    public String language() {
        return language;
    }

    @Override
    public EditorType editor() {
        return editor;
    }
}
