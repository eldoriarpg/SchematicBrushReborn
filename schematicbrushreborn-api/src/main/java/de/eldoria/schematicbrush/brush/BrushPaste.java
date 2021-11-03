package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import de.eldoria.schematicbrush.schematics.Schematic;

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
}
