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

package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.rendering.BlockChangeCollector;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;

/**
 * A brush used to paste schematics.
 */
public interface SchematicBrush extends Brush {
    @Override
    void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size);

    /**
     * Paste the brush and capture changes.
     *
     * @return changes which will be made to the world
     */
    BlockChangeCollector pasteFake();

    /**
     * Get the settings of the brush
     *
     * @return settings
     */
    BrushSettings getSettings();

    /**
     * Get the next paste which will be executed
     *
     * @return next paste
     */
    BrushPaste nextPaste();

    /**
     * Convert the settings of the brush to a builder
     *
     * @param settingsRegistry  settings registry
     * @param schematicRegistry schematic registry
     * @return brush as builder
     */
    BrushBuilder toBuilder(BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry);
}
