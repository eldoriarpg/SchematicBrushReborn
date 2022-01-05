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


    @Override
    public void refresh() {
        schematicSet.refreshMutator();
        settings.refreshMutator();
    }

    @Override
    public boolean shiftFlip() {
        reloadSchematic();
        schematicSet.getMutator(SchematicModifier.FLIP).shift();
        return schematicSet.getMutator(SchematicModifier.FLIP).shiftable();
    }

    @Override
    public boolean shiftRotation() {
        reloadSchematic();
        schematicSet.getMutator(SchematicModifier.ROTATION).shift();
        return schematicSet.getMutator(SchematicModifier.ROTATION).shiftable();
    }

    @Override
    public boolean shiftOffset() {
        reloadSchematic();
        settings.getMutator(PlacementModifier.OFFSET).shift();
        return settings.getMutator(PlacementModifier.OFFSET).shiftable();
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
        var pasteMutation = new PasteMutationImpl(owner.getPlayer(), clipboard, editSession);
        settings.mutate(pasteMutation);
        schematicSet.mutate(pasteMutation);
        var clipboardHolder = buildClipboard(pasteMutation);
        return paste(clipboardHolder, capturingExtent, position, pasteMutation);
    }

    private Operation paste(ClipboardHolder clipboardHolder, Extent targetExtent, BlockVector3 position, PasteMutation mutation) {
        // Create paste operation
        return clipboardHolder.createPaste(targetExtent)
                .to(position.add(mutation.pasteOffset()))
                .ignoreAirBlocks(!mutation.isIncludeAir())
                .maskSource(mutation.maskSource())
                .build();
    }

    private ClipboardHolder buildClipboard(PasteMutation mutation) {
        var clipboardHolder = new ClipboardHolder(clipboard);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(mutation.transform()));
        return clipboardHolder;
    }

    /**
     * Shift to the next schematic
     *
     * @return true if the schematic was changed
     */
    @Override
    public boolean shiftSchematic() {
        if (schematicSet.schematics().size() <= 1) return false;
        var newSchematic = schematicSet.getRandomSchematic();
        while (newSchematic == schematic) {
            newSchematic = schematicSet.getRandomSchematic();
        }

        schematic = newSchematic;
        reloadSchematic();
        return true;
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
