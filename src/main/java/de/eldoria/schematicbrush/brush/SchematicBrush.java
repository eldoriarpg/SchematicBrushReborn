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
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.parameter.Flip;
import de.eldoria.schematicbrush.brush.config.parameter.Placement;
import de.eldoria.schematicbrush.brush.config.parameter.Rotation;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents the schematic brush as a {@link Brush} instance. A brush is immutable after creation and is always
 * assigned to only one player.
 */
public class SchematicBrush implements Brush {

    private final Plugin plugin;
    private final BrushSettings settings;
    private final Player brushOwner;

    /**
     * Create a new schematic brush for a player.
     *
     * @param plugin
     * @param player   placer which owns this brush
     * @param settings settings of the brush
     */
    public SchematicBrush(Plugin plugin, Player player, BrushSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        brushOwner = player;
    }

    @Override
    public void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size)
            throws MaxChangedBlocksException {
        SchematicSet randomSchematicSet = settings.getRandomBrushConfig();

        Clipboard clipboard = randomSchematicSet.getRandomSchematic();

        if (clipboard == null) {
            MessageSender.getPluginMessageSender(plugin).sendError(brushOwner, "No valid schematic was found for brush: "
                    + randomSchematicSet.arguments());
            return;
        }

        // Apply flip
        Flip direction = randomSchematicSet.flip().getFlipDirection();
        AffineTransform transform = new AffineTransform();
        if (direction != Flip.NONE) {
            transform = transform.scale(direction.asVector().abs().multiply(-2).add(1, 1, 1));
        }

        // Apply rotation
        Rotation rotation = randomSchematicSet.rotation();
        transform = transform.rotateY(rotation.getDeg());

        // Save current user mask
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

        // Create a new clipboard holder and apply the transforms
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(brushOwner));
        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
        localSession.setClipboard(clipboardHolder);
        clipboardHolder.setTransform(clipboardHolder.getTransform().combine(transform));

        // Find center of schematic and set new origin
        BlockVector3 dimensions = clipboard.getDimensions();
        if (settings.placement() != Placement.ORIGINAL) {
            int centerZ = clipboard.getMinimumPoint().getBlockZ() + dimensions.getBlockZ() / 2;
            int centerX = clipboard.getMinimumPoint().getBlockX() + dimensions.getBlockX() / 2;
            int centerY = clipboard.getMinimumPoint().getBlockY() + settings.placement().find(clipboard);
            clipboard.setOrigin(BlockVector3.at(centerX, centerY, centerZ));
        }

        // Create paste operation
        PasteBuilder paste = clipboardHolder.createPaste(editSession);
        Operation operation = paste
                .to(position.add(0, settings.yOffset(), 0))
                .ignoreAirBlocks(!settings.isIncludeAir())
                .build();

        Operations.completeBlindly(operation);
    }

    /**
     * Combine the current configuration with a new brush configuration to get a new brush
     *
     * @param brush Brush to combine. Only the {@link SchematicSet} list is updated.
     * @return a new schematic brush with the sub brushes of both brush configurations.
     */
    public SchematicBrush combineBrush(BrushSettings brush) {
        return new SchematicBrush(plugin, brushOwner, settings.combine(brush));
    }

    public BrushSettings getSettings() {
        return settings;
    }
}
