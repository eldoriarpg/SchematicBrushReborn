/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.world.item.ItemType;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utility class to manage world edit brushes.
 */
public final class WorldEditBrush {
    private static final WorldEdit WORLD_EDIT = WorldEdit.getInstance();

    private WorldEditBrush() {
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
     * @param player   player for lookup
     * @param material material to get the registered brush
     * @return schematic brush instance if the item is a schematic brush
     */
    public static Optional<SchematicBrush> getSchematicBrush(Player player, Material material) {
        if (getBrush(player, material) instanceof SchematicBrush brush) {
            return Optional.of(brush);
        }
        return Optional.empty();
    }

    /**
     * Get the schematic brush of a player registered on the item in its main hand.
     *
     * @param player player for lookup
     * @return schematic brush instance if the item is a schematic brush
     */
    @Nullable
    public static Brush getBrush(Player player) {
        return getBrush(player, player.getInventory().getItemInMainHand().getType());
    }

    /**
     * Get the schematic brush of a player registered on the item in its main hand.
     *
     * @param player   player for lookup
     * @param material material to get the brush
     * @return schematic brush instance if the item is a schematic brush
     */
    @SuppressWarnings({"ProhibitedExceptionCaught"})
    @Nullable
    public static Brush getBrush(Player player, Material material) {
        var itemType = BukkitAdapter.asItemType(material);
        if (itemType == null || itemType.hasBlockType()) {
            return null;
        }
        try {
            if (getLocalSession(player).getTool(itemType) instanceof BrushTool brushTool) {
                return brushTool.getBrush();
            }
        } catch (NullPointerException e) {
            // for some reason world edit throws a NPE when this function is called on world edit tools
        }
        return null;
    }

    /**
     * Set the brush for a player and the item in its main hand.
     *
     * @param player player to set
     * @param brush  brush to set
     * @return true if the brush was set.
     */
    public static boolean setBrush(Player player, Brush brush) {
        return setBrush(player, brush, "schematicbrush.brush.use");
    }

    /**
     * Set the brush for a player and the item in its main hand.
     *
     * @param player player to set
     * @param brush  brush to set
     * @param permission the permission required to use the brush
     * @return true if the brush was set.
     */
    public static boolean setBrush(Player player, Brush brush, String permission) {
        var stack = player.getInventory().getItemInMainHand();
        return setBrush(player, stack, brush, permission);
    }

    /**
     * Set the brush for a player and the item in its main hand.
     *
     * @param player player to set
     * @param stack the stack to bind the brush to
     * @param brush  brush to set
     * @param permission the permission required to use the brush
     * @return true if the brush was set.
     */
    public static boolean setBrush(Player player, ItemStack stack, Brush brush, String permission) {
        try {
            var brushTool = new BrushTool(permission);
            brushTool.setBrush(brush, permission);
            if (toItemType(stack).hasBlockType()) {
                MessageSender.getPluginMessageSender(SchematicBrushReborn.class).sendError(player, "Can not bind brush on a block.");
                return false;
            }
            if (FAWE.isFawe()) {
                getLocalSession(player).setTool(toItemType(stack).getDefaultState(), brushTool, BukkitAdapter.adapt(player));
            } else {
                getLocalSession(player).setTool(toItemType(stack), brushTool);
            }
        } catch (InvalidToolBindException e) {
            MessageSender.getPluginMessageSender(SchematicBrushReborn.class).sendError(player, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Remove the brush for a player of the item in its main hand.
     *
     * @param player player to set
     * @return true if the brush was remove.
     */
    public static boolean removeBrush(Player player) {
        var stack = player.getInventory().getItemInMainHand();
        return removeBrush(player, stack);
    }

    /**
     * Remove a brush for this player on the item stack type..
     *
     * @param player player to set
     * @param stack  the item stack
     * @return true if the brush was removed.
     */
    public static boolean removeBrush(Player player, ItemStack stack) {
        try {
            if (FAWE.isFawe()) {
                getLocalSession(player).setTool(toItemType(stack).getDefaultState(), null, BukkitAdapter.adapt(player));
            } else {
                getLocalSession(player).setTool(toItemType(stack), null);
            }
        } catch (InvalidToolBindException e) {
            MessageSender.getPluginMessageSender(SchematicBrushReborn.class).sendError(player, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Checks if the stack is not of type block.
     *
     * @param stack stack
     * @return true if the item stack is not of type block
     */
    public static boolean canBeBound(ItemStack stack) {
        return !toItemType(stack).hasBlockType();
    }

    public static ItemType toItemType(ItemStack stack) {
        return BukkitAdapter.asItemType(stack.getType());
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
