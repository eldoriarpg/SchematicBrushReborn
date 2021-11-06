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

package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.core.EldoUtilities;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * The base of the schematic brush plugin.
 */
@SuppressWarnings("unused")
public abstract class SchematicBrushReborn extends EldoPlugin {

    public SchematicBrushReborn() {
        EldoUtilities.forceInstanceOwner(this);
    }

    public SchematicBrushReborn(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
        EldoUtilities.forceInstanceOwner(this);
    }

    /**
     * Get schematic registry
     *
     * @return schematic registry
     */
    public abstract SchematicRegistry schematics();

    /**
     * Get brush settings registry
     *
     * @return brush settings registry
     */
    public abstract BrushSettingsRegistry brushSettingsRegistry();

    /**
     * Get the plugin config
     *
     * @return plugin config
     */
    public abstract Configuration config();
}
