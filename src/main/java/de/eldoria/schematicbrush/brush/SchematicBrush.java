package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.util.Flip;
import de.eldoria.schematicbrush.util.Rotation;
import org.bukkit.entity.Player;

import java.io.IOException;

public class SchematicBrush implements Brush {

    private final BrushSettings settings;
    private final Player brushOwner;

    public SchematicBrush(Player player, BrushSettings settings) {
        this.settings = settings;
        brushOwner = player;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size) throws MaxChangedBlocksException {
        BrushConfig randomBrushConfig = settings.getRandomBrushConfig();

        Schematic randomSchematic = randomBrushConfig.getRandomSchematic();
        Clipboard clipboard = null;

        while (clipboard == null && randomSchematic != null) {
            try {
                clipboard = randomSchematic.getSchematic();
            } catch (IOException e) {
                // Silently fail and search for another schematic.
            }
        }

        if (randomSchematic == null) {
            MessageSender.sendError(brushOwner, "No valid schematic was found for this brush.");
            return;
        }

        // Apply flip
        Flip direction = randomBrushConfig.getFlip().getFlipDirection();
        AffineTransform transform = new AffineTransform();
        if (direction != Flip.NONE) {
            transform = transform.scale(direction.asVector().abs().multiply(-2).add(1, 1, 1));
        }

        // Apply rotation
        Rotation rotation = randomBrushConfig.getRotation();
        transform = transform.rotateY(rotation.getDeg());


        // Apply replace mask
        if (settings.isReplaceAirOnly()) {
            editSession.setMask(
                    new BlockTypeMask(editSession, BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR));
        }

        // Create a new clipboard holder and apply the transforms
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(brushOwner));
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
        localSession.setClipboard(clipboardHolder);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));

        // Find center of schematic and set with offset
        BlockVector3 dimensions = clipboard.getDimensions();
        int centerZ = clipboard.getMinimumPoint().getBlockZ() + dimensions.getBlockZ() / 2;
        int centerX = clipboard.getMinimumPoint().getBlockX() + dimensions.getBlockX() / 2;
        int centerY = clipboard.getMinimumPoint().getBlockY() + settings.getPlacement().find(clipboard);
        clipboard.setOrigin(BlockVector3.at(centerX, centerY, centerZ));

        // Create paste operation
        PasteBuilder paste = clipboardHolder.createPaste(editSession);
        Operation operation = paste
                .to(position.add(0, settings.getYOffset(), 0))
                .ignoreAirBlocks(!settings.isIncludeAir())
                .build();

        Operations.completeBlindly(operation);
        brushOwner.sendMessage("brush executed");
    }
}
