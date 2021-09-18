package de.eldoria.schematicbrush.commands.util;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.HandSide;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class WorldEditBrushAdapter {
    private static final WorldEdit WORLD_EDIT = WorldEdit.getInstance();

    private WorldEditBrushAdapter() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Get the schematic brush of a player registered on the item in its main hand.
     *
     * @param player player for lookup
     * @return schematic brush instance if the item is a schematic brush
     */
    public static Optional<SchematicBrush> getSchematicBrush(Player player) {
        var itemInMainHand = player.getInventory().getItemInMainHand();
        return getSchematicBrush(player, itemInMainHand.getType());
    }

    /**
     * Get the schematic brush of a player registered on the item in its main hand.
     *
     * @param player player for lookup
     * @return schematic brush instance if the item is a schematic brush
     */
    public static Optional<SchematicBrush> getSchematicBrush(Player player, Material material) {
        var itemType = BukkitAdapter.asItemType(material);
        if (itemType == null || itemType.hasBlockType()) {
            return Optional.empty();
        }
        try {
            var brushTool = getLocalSession(player).getBrushTool(itemType);
            if (brushTool.getBrush() != null && brushTool.getBrush() instanceof SchematicBrush) {
                return Optional.of((SchematicBrush) brushTool.getBrush());
            }
        } catch (InvalidToolBindException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Set the brush for a player and the item in its main hand.
     *
     * @param player player to set
     * @param brush  brush to set
     * @return true if the brush was set.
     */
    public static boolean setBrush(Player player, Brush brush) {
        var itemInMainHand = player.getInventory().getItemInMainHand();
        try {
            getLocalSession(player).getBrushTool(BukkitAdapter.asItemType(itemInMainHand.getType())).setBrush(brush, "schematicbrush.brush.use");
        } catch (InvalidToolBindException e) {
            MessageSender.getPluginMessageSender(SchematicBrushReborn.class).sendError(player, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Get the local session of a player
     *
     * @param player player for lookup
     * @return local session.
     */
    private static LocalSession getLocalSession(Player player) {
        Actor actor = BukkitAdapter.adapt(player);

        return WORLD_EDIT.getSessionManager().get(actor);
    }
}
