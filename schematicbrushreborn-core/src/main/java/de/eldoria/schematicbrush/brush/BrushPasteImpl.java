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
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Shiftable;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Represents the next paste executed by the brush.
 */
public class BrushPasteImpl implements BrushPaste {
    private final SchematicBrush brush;
    private final BrushSettings settings;
    private SchematicSet schematicSet;
    private Schematic schematic;
    private Clipboard clipboard;

    public BrushPasteImpl(SchematicBrush brush, BrushSettings settings, SchematicSet schematicSet, Schematic schematic) {
        this.brush = brush;
        this.settings = settings;
        this.schematicSet = schematicSet;
        this.schematic = schematic;
        reloadSchematic();
        refresh();
    }


    @Override
    public final void refresh() {
        schematicSet.refreshMutator();
        settings.refreshMutator();
    }

    @Override
    public boolean shiftFlip() {
        reloadSchematic();
        return shift(schematicSet.getMutator(SchematicModifier.FLIP), settings.getMutator(PlacementModifier.FLIP));
    }

    @Override
    public boolean shiftRotation() {
        reloadSchematic();
        return shift(schematicSet.getMutator(SchematicModifier.ROTATION), settings.getMutator(PlacementModifier.ROTATION));
    }

    @Override
    public boolean shiftOffset() {
        reloadSchematic();
        return shift(schematicSet.getMutator(SchematicModifier.OFFSET), settings.getMutator(PlacementModifier.OFFSET));
    }

    private boolean shift(Mutator<?>... mutators) {
        var shifts = new ArrayList<>(Arrays.asList(mutators));
        shifts.removeIf(Objects::isNull);
        if (shifts.isEmpty()) return false;
        shifts.forEach(Mutator::shift);
        return shifts.stream().anyMatch(Shiftable::shiftable);
    }

    @Override
    public Mutator<?> flip() {
        return schematicSet.getMutator(SchematicModifier.FLIP);
    }

    @Override
    public Mutator<?> rotation() {
        return schematicSet.getMutator(SchematicModifier.ROTATION);
    }

    @Override
    public Mutator<?> offsetSet() {
        reloadSchematic();
        return schematicSet.getMutator(SchematicModifier.OFFSET);
    }

    @Override
    public Mutator<?> offsetBrush() {
        reloadSchematic();
        return settings.getMutator(PlacementModifier.OFFSET);
    }

    @Override
    public BrushSettings settings() {
        return settings;
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
    public boolean nextSchematic() {
        brush.history().push(schematicSet, schematic);
        var next = settings.nextSchematic(brush);
        if (schematicSet.schematics().isEmpty() || next.isEmpty()) return false;
        next.ifPresent(pair -> {
            schematicSet = pair.first;
            schematic = pair.second;
        });
        reloadSchematic();
        return true;
    }

    @Override
    public boolean previousSchematic() {
        var next = brush.history().previous();
        if (schematicSet.schematics().isEmpty() || next.isEmpty()) return false;
        schematicSet = next.get().first;
        schematic = next.get().second;
        reloadSchematic();
        return true;
    }

    /**
     * Load a new clipboard from schematic file\
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
     * Current schematic set
     *
     * @return schematic set
     */
    @Override
    public SchematicSet schematicSet() {
        return schematicSet;
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

    @Override
    public SchematicBrush brush() {
        return brush;
    }
}
