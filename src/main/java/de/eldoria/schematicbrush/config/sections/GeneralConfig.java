package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@SerializableAs("sbrGeneralSettings")
public class GeneralConfig implements ConfigurationSerializable {
    private boolean checkUpdates = true;
    private boolean previewDefault = true;
    private boolean showNameDefault = false;
    private int previewRefreshInterval = 1;
    private int maxRenderMs = 25;
    private int maxRenderSize = 2500;

    public GeneralConfig() {
    }

    public GeneralConfig(Map<String, Object> objectMap) {
        SerializationUtil.mapOnObject(objectMap, this);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.objectToMap(this);
    }

    public boolean isCheckUpdates() {
        return checkUpdates;
    }

    public boolean isPreviewDefault() {
        return previewDefault;
    }

    public boolean isShowNameDefault() {
        return showNameDefault;
    }

    public int previewRefreshInterval() {
        return previewRefreshInterval;
    }

    public int maxRenderMs() {
        return maxRenderMs;
    }

    public int maxRenderSize() {
        return maxRenderSize;
    }
}
