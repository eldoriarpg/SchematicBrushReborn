package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.schematicbrush.brush.config.parameter.Placement;

public class BrushPaste {
    final BrushSettings settings;
    final SchematicSet schematicSet;
    final Clipboard origClipboard;
    Vector3 direction;
    int rotation;
    int offset;
    private AffineTransform transform = new AffineTransform();

    public BrushPaste(BrushSettings settings, SchematicSet schematicSet, Clipboard origClipboard) {
        this.settings = settings;
        this.schematicSet = schematicSet;
        this.origClipboard = origClipboard;
        changeDirection();
        changeFlip();
        changeOffset();
    }

    public void changeDirection() {
        direction = schematicSet.flip().asVector();
    }

    public void changeFlip() {
        rotation = schematicSet.rotation().getDeg();
    }

    public void changeOffset() {
        offset = settings.yOffset().offset();
    }

    private BrushPaste flip() {
        transform = transform.scale(direction.abs().multiply(-2).add(1, 1, 1));
        return this;
    }

    private BrushPaste rotate() {
        transform = transform.rotateY(rotation);
        return this;
    }

    private BrushPaste replaceAll(EditSession editSession) {
        Mask preBrushMask = editSession.getMask();
        // Apply replace mask
        if (!settings.isReplaceAll()) {
            // Check if the user has a block mask defined and append if present.
            //Mask mask = WorldEditBrushAdapter.getMask(brushOwner);
            if (preBrushMask instanceof BlockTypeMask) {
                BlockTypeMask blockMask = (BlockTypeMask) preBrushMask;
                blockMask.add(BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR);
            } else {
                editSession.setMask(
                        new BlockTypeMask(editSession, BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR));
            }
        }
        return this;
    }

    public Operation buildpaste(EditSession editSession, BukkitPlayer owner, BlockVector3 position) {
        flip();
        rotate();
        replaceAll(editSession);
        ClipboardHolder clipboardHolder = buildClipboard(owner);
        center();
        return paste(clipboardHolder, editSession, position);
    }

    private Operation paste(ClipboardHolder clipboardHolder, EditSession editSession, BlockVector3 position) {
        // Create paste operation
        PasteBuilder paste = clipboardHolder.createPaste(editSession);
        return paste
                .to(position.add(0, settings.yOffset().offset(), 0))
                .ignoreAirBlocks(!settings.isIncludeAir())
                .build();
    }

    private ClipboardHolder buildClipboard(BukkitPlayer owner) {
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(owner);
        ClipboardHolder clipboardHolder = new ClipboardHolder(origClipboard);
        localSession.setClipboard(clipboardHolder);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));
        return clipboardHolder;
    }

    private void center() {
        BlockVector3 dimensions = origClipboard.getDimensions();
        if (settings.placement() != Placement.ORIGINAL) {
            int centerZ = origClipboard.getMinimumPoint().getBlockZ() + dimensions.getBlockZ() / 2;
            int centerX = origClipboard.getMinimumPoint().getBlockX() + dimensions.getBlockX() / 2;
            int centerY = origClipboard.getMinimumPoint().getBlockY() + settings.placement().find(origClipboard);
            origClipboard.setOrigin(BlockVector3.at(centerX, centerY, centerZ));
        }
    }
}
