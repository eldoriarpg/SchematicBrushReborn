/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
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
     *
     * @return true if flip was shiftable
     */
    boolean shiftFlip();

    /**
     * Shift to next rotation value
     *
     * @return true if rotation was shiftable
     */
    boolean shiftRotation();

    /**
     * The flip mutator
     *
     * @return flip mutator
     */
    Mutator<?> flip();

    /**
     * The rotation mutator
     *
     * @return rotation mutator
     */
    Mutator<?> rotation();

    /**
     * The offset mutator of the set
     *
     * @return offset mutator
     */
    Mutator<?> offsetSet();

    /**
     * The offset mutator of the brush
     *
     * @return offset mutator
     */
    Mutator<?> offsetBrush();

    /**
     * The brush settings
     *
     * @return settings of the brush
     */
    BrushSettings settings();

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
     *
     * @return true if the schematic changed
     */
    boolean nextSchematic();

    /**
     * Shift to the previous schematic
     *
     * @return true if the schematic was changed
     */
    boolean previousSchematic();

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
     * Current schematic set
     *
     * @return schematic set
     * @since 2.0.2
     */
    SchematicSet schematicSet();

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
     *
     * @return true if the offset was shiftable
     */
    boolean shiftOffset();

    /**
     * Get the brush which will paste this paste.
     *
     * @return schematic brush
     */
    SchematicBrush brush();
}
