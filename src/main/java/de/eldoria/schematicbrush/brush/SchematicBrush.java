package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.schematicbrush.commands.util.MessageSender;
import de.eldoria.schematicbrush.brush.config.BrushConfiguration;
import de.eldoria.schematicbrush.brush.config.SubBrush;
import de.eldoria.schematicbrush.brush.config.parameter.Flip;
import de.eldoria.schematicbrush.brush.config.parameter.Rotation;
import org.bukkit.entity.Player;

/**
 * Represents the schematic brush as a {@link Brush} instance.
 * A brush is immutable after creation and is always assigned to only one player.
 */
public class SchematicBrush implements Brush {

    private final BrushConfiguration settings;
    private final Player brushOwner;

    public SchematicBrush(Player player, BrushConfiguration settings) {
        this.settings = settings;
        brushOwner = player;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size)
            throws MaxChangedBlocksException {
        SubBrush randomSubBrush = settings.getRandomBrushConfig();

        Clipboard clipboard = randomSubBrush.getRandomSchematic();

        if (clipboard == null) {
            MessageSender.sendError(brushOwner, "No valid schematic was found for brush: "
                    + randomSubBrush.getArguments());
            return;
        }

        // Apply flip
        Flip direction = randomSubBrush.getFlip().getFlipDirection();
        AffineTransform transform = new AffineTransform();
        if (direction != Flip.NONE) {
            transform = transform.scale(direction.asVector().abs().multiply(-2).add(1, 1, 1));
        }

        // Apply rotation
        Rotation rotation = randomSubBrush.getRotation();
        transform = transform.rotateY(rotation.getDeg());

        // Save current user mask
        Mask preBrushMask = editSession.getMask();

        // Apply replace mask
        if (settings.isReplaceAirOnly()) {
            // Check if the user has a block mask defined and append if present.
            if (editSession.getMask() != null && editSession.getMask() instanceof BlockTypeMask) {
                BlockTypeMask mask = (BlockTypeMask) editSession.getMask();
                mask.add(BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR);
            } else {
                editSession.setMask(
                        new BlockTypeMask(editSession, BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR));
            }
        }

        // Create a new clipboard holder and apply the transforms
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(brushOwner));
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
        localSession.setClipboard(clipboardHolder);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));

        // Find center of schematic and set new origin
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

        editSession.setMask(preBrushMask);
    }

    /**
     * Combine the current configuration with a new brush configuration to get a new brush
     *
     * @param brush Brush to combine. Only the {@link SubBrush} list is updated.
     * @return a new schematic brush with the sub brushes of both brush configurations.
     */
    public SchematicBrush combineBrush(BrushConfiguration brush) {
        return new SchematicBrush(brushOwner, settings.combine(brush));
    }

    public BrushConfiguration getSettings() {
        return settings;
    }
}
