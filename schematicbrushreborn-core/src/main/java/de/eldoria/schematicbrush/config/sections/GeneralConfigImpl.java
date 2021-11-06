/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
