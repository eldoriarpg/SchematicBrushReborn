package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.eldoutilities.utils.EMath;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.parameter.Placement;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.io.IOException;
import java.util.logging.Level;

public class BrushPaste {
    private final BrushSettings settings;
    private final SchematicSet schematicSet;
    private Schematic schematic;
    private Clipboard clipboard;
    private AffineTransform transform = new AffineTransform();
    private boolean flipped;
    private boolean rotated;
    private boolean centered;

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
        schematicSet.flip().refresh();
    }

    public void changeRotation() {
        schematicSet.rotation().refresh();
    }

    public void changeOffset() {
        settings.yOffset().refresh();
    }

    public void shiftFlip() {
        reloadSchematic();
        schematicSet.flip().shift();
    }

    public void shiftRotation() {
        reloadSchematic();
        schematicSet.rotation().shift();
    }

    public void shiftOffset() {
        reloadSchematic();
        settings.yOffset().shift();
    }

    private void flip() {
        if (flipped) return;
        if (schematicSet.flip().value().direction() != Vector3.ZERO) {
            transform = transform.scale(schematicSet.flip().value().direction().abs().multiply(-2).add(1, 1, 1));
        }
        flipped = true;
    }

    private void rotate() {
        if (rotated) return;
        if (schematicSet.rotation().value().degree() != 0) {
            transform = transform.rotateY(schematicSet.rotation().value().degree());
        }
        rotated = true;
    }

    private void replaceAll(EditSession editSession) {
        var preBrushMask = editSession.getMask();
        // Apply replace mask
        if (!settings.isReplaceAll()) {
            // Check if the user has a block mask defined and append if present.
            //Mask mask = WorldEditBrushAdapter.getMask(brushOwner);
            if (preBrushMask instanceof BlockTypeMask) {
                var blockMask = (BlockTypeMask) preBrushMask;
                blockMask.add(BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR);
            } else {
                editSession.setMask(
                        new BlockTypeMask(editSession, BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR));
            }
        }
    }

    public Operation buildpaste(EditSession editSession, BukkitPlayer owner, BlockVector3 position) {
        return buildpaste(editSession, editSession, owner, position);
    }

    public Operation buildpaste(EditSession editSession, Extent capturingExtent, BukkitPlayer owner, BlockVector3 position) {
        flip();
        rotate();
        replaceAll(editSession);
        var clipboardHolder = buildClipboard(owner);
        center();
        return paste(clipboardHolder, capturingExtent, position);
    }

    private Operation paste(ClipboardHolder clipboardHolder, Extent targetExtent, BlockVector3 position) {
        // Create paste operation
        var paste = clipboardHolder.createPaste(targetExtent);
        return paste
                .to(position.add(0, settings.yOffset().value(), 0))
                .ignoreAirBlocks(!settings.isIncludeAir())
                .build();
    }

    private ClipboardHolder buildClipboard(BukkitPlayer owner) {
        var localSession = WorldEdit.getInstance().getSessionManager().get(owner);
        var clipboardHolder = new ClipboardHolder(clipboard);
        localSession.setClipboard(clipboardHolder);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));
        return clipboardHolder;
    }

    private void center() {
        if (centered) return;
        var dimensions = clipboard.getDimensions();
        if (settings.placement() != Placement.ORIGINAL) {
            var centerZ = clipboard.getMinimumPoint().getBlockZ() + dimensions.getBlockZ() / 2;
            var centerX = clipboard.getMinimumPoint().getBlockX() + dimensions.getBlockX() / 2;
            var centerY = clipboard.getMinimumPoint().getBlockY() + settings.placement().find(clipboard);
            clipboard.setOrigin(BlockVector3.at(centerX, centerY, centerZ));
        }
        centered = true;
    }

    public void shiftSchematic() {
        schematic = schematicSet.getRandomSchematic();
        reloadSchematic();
    }

    public void reloadSchematic() {
        centered = false;
        rotated = false;
        flipped = false;
        transform = new AffineTransform();
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
