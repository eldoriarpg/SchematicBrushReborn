package de.eldoria.schematicbrush.brush.config;

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
import de.eldoria.schematicbrush.schematics.Schematic;

import java.io.IOException;
import java.util.logging.Level;

public class BrushPaste {
    private final BrushSettings settings;
    private final SchematicSet schematicSet;
    private Schematic schematic;
    private Clipboard clipboard;

    public BrushPaste(BrushSettings settings, SchematicSet schematicSet, Schematic schematic) {
        this.settings = settings;
        this.schematicSet = schematicSet;
        this.schematic = schematic;
        reloadSchematic();
        changeFlip();
        changeRotation();
        changeOffset();
    }

    public void changeFlip() {
        schematicSet.getMutator(SchematicModifier.FLIP).refresh();
    }

    public void changeRotation() {
        schematicSet.getMutator(SchematicModifier.ROTATION).refresh();
    }

    public void changeOffset() {
        settings.getMutator(PlacementModifier.OFFSET).refresh();
    }

    public void shiftFlip() {
        reloadSchematic();
        schematicSet.getMutator(SchematicModifier.FLIP).shift();
    }

    public void shiftRotation() {
        reloadSchematic();
        schematicSet.getMutator(SchematicModifier.ROTATION).shift();
    }

    public void shiftOffset() {
        reloadSchematic();
        settings.getMutator(PlacementModifier.OFFSET).shift();
    }

    public Operation buildpaste(EditSession editSession, BukkitPlayer owner, BlockVector3 position) {
        return buildpaste(editSession, editSession, owner, position);
    }

    public Operation buildpaste(EditSession editSession, Extent capturingExtent, BukkitPlayer owner, BlockVector3 position) {
        var pasteMutation = new PasteMutation(clipboard, editSession);
        // TODO: Check for infinite rotation and flip again.
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

    public void shiftSchematic() {
        schematic = schematicSet.getRandomSchematic();
        reloadSchematic();
    }

    public void reloadSchematic() {
        try {
            clipboard = schematic.loadSchematic();
        } catch (IOException e) {
            SchematicBrushReborn.logger().log(Level.SEVERE, "Could not load schemartic", e);
        }
    }

    public Schematic schematic() {
        return schematic;
    }

    public Clipboard clipboard() {
        return clipboard;
    }

    public long clipboardSize() {
        var minimumPoint = clipboard.getMinimumPoint();
        var maximumPoint = clipboard.getMaximumPoint();
        var x = (long) EMath.diff(minimumPoint.getBlockX(), maximumPoint.getBlockX());
        var y = (long) EMath.diff(minimumPoint.getBlockY(), maximumPoint.getBlockY());
        var z = (long) EMath.diff(minimumPoint.getBlockZ(), maximumPoint.getBlockZ());
        return x * y * z;
    }
}
