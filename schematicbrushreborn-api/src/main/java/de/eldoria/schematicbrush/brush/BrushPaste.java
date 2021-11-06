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
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import de.eldoria.schematicbrush.schematics.Schematic;

/**
 * Represents the next pending brush operation.
 */
public interface BrushPaste {
    /**
     * Refresh all mutator values
     */
    void refresh();

    /**
     * Shift to next flip value
     */
    void shiftFlip();

    /**
     * Shift to next rotation value
     */
    void shiftRotation();

    /**
     * Build a paste operation
     *
     * @param editSession edit session
     * @param owner       owner of brush
     * @param position    position to paste
     * @return operation
     */
    Operation buildpaste(EditSession editSession, BukkitPlayer owner, BlockVector3 position);

    /**
     * Build a paste operation
     *
     * @param editSession     edit session
     * @param capturingExtent extend to caputure changes
     * @param owner           owner of brush
     * @param position        position to paste
     * @return operation
     */
    Operation buildpaste(EditSession editSession, Extent capturingExtent, BukkitPlayer owner, BlockVector3 position);

    /**
     * Shift to the next schematic
     */
    void shiftSchematic();

    /**
     * Load a new clipboard from schematic file
     */
    void reloadSchematic();

    /**
     * Current schematic
     *
     * @return schematic
     */
    Schematic schematic();

    /**
     * Current clipboard
     *
     * @return clipboard
     */
    Clipboard clipboard();

    /**
     * Size of the clipboard in blocks
     *
     * @return true
     */
    long clipboardSize();

    /**
     * Shift to the next offset value.
     */
    void shiftOffset();
}
