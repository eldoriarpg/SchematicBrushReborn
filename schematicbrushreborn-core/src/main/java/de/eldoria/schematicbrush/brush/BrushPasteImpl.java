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
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.eldoria.eldoutilities.utils.EMath;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Represents the next paste executed by the brush.
 */
public class BrushPasteImpl implements BrushPaste {
    private final BrushSettings settings;
    private final SchematicSet schematicSet;
    private Schematic schematic;
    private Clipboard clipboard;

    public BrushPasteImpl(BrushSettings settings, SchematicSet schematicSet, Schematic schematic) {
        this.settings = settings;
        this.schematicSet = schematicSet;
        this.schematic = schematic;
        reloadSchematic();
        refresh();
    }

    /**
     * Refresh all mutator values
     */
    @Override
    public void refresh() {
        schematicSet.refreshMutator();
    }

    /**
     * Shift to next flip value
     */
    @Override
    public void shiftFlip() {
        reloadSchematic();
        schematicSet.getMutator(SchematicModifier.FLIP).shift();
    }

    /**
     * Shift to next rotation value
     */
    @Override
    public void shiftRotation() {
        reloadSchematic();
        schematicSet.getMutator(SchematicModifier.ROTATION).shift();
    }

    @Override
    public void shiftOffset() {
        reloadSchematic();
        settings.getMutator(PlacementModifier.OFFSET).shift();
    }

    /**
     * Build a paste operation
     *
     * @param editSession edit session
     * @param owner       owner of brush
     * @param position    position to paste
     * @return operation
     */
    @Override
    public Operation buildpaste(EditSession editSession, BukkitPlayer owner, BlockVector3 position) {
        return buildpaste(editSession, editSession, owner, position);
    }

    /**
     * Build a paste operation
     *
     * @param editSession     edit session
     * @param capturingExtent extend to caputure changes
     * @param owner           owner of brush
     * @param position        position to paste
     * @return operation
     */
    @Override
    public Operation buildpaste(EditSession editSession, Extent capturingExtent, BukkitPlayer owner, BlockVector3 position) {
        var pasteMutation = new PasteMutationImpl(clipboard, editSession);
        settings.mutate(pasteMutation);
        schematicSet.mutate(pasteMutation);
        var clipboardHolder = buildClipboard(owner, pasteMutation);
        return paste(clipboardHolder, capturingExtent, position, pasteMutation);
    }

    private Operation paste(ClipboardHolder clipboardHolder, Extent targetExtent, BlockVector3 position, PasteMutation mutation) {
        // Create paste operation
        var paste = clipboardHolder.createPaste(targetExtent);
        return paste
                .to(position.add(mutation.pasteOffset()))
                .ignoreAirBlocks(!mutation.isIncludeAir())
                .build();
    }

    private ClipboardHolder buildClipboard(BukkitPlayer owner, PasteMutation mutation) {
        var localSession = WorldEdit.getInstance().getSessionManager().get(owner);
        var clipboardHolder = new ClipboardHolder(clipboard);
        localSession.setClipboard(clipboardHolder);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(mutation.transform()));
        return clipboardHolder;
    }

    /**
     * Shift to the next schematic
     */
    @Override
    public void shiftSchematic() {
        schematic = schematicSet.getRandomSchematic();
        reloadSchematic();
    }

    /**
     * Load a new clipboard from schematic file
     */
    @Override
    public final void reloadSchematic() {
        try {
            clipboard = schematic.loadSchematic();
        } catch (IOException e) {
            SchematicBrushReborn.logger().log(Level.SEVERE, "Could not load schematic", e);
            clipboard = null;
        }
    }

    /**
     * Current schematic
     *
     * @return schematic
     */
    @Override
    public Schematic schematic() {
        return schematic;
    }

    /**
     * Current clipboard
     *
     * @return clipboard
     */
    @Override
    public Clipboard clipboard() {
        return clipboard;
    }

    /**
     * Size of the clipboard in blocks
     *
     * @return true
     */
    @Override
    public long clipboardSize() {
        var minimumPoint = clipboard.getMinimumPoint();
        var maximumPoint = clipboard.getMaximumPoint();
        return (long) EMath.diff(minimumPoint.getBlockX(), maximumPoint.getBlockX())
               * EMath.diff(minimumPoint.getBlockY(), maximumPoint.getBlockY())
               * EMath.diff(minimumPoint.getBlockZ(), maximumPoint.getBlockZ());
    }
}
