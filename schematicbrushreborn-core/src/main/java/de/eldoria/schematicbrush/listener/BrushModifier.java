/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BrushModifier implements Listener {
    private final MessageSender messageSender = MessageSender.getPluginMessageSender(SchematicBrushRebornImpl.class);

    @EventHandler(priority = EventPriority.LOW)
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;
        var schematicBrush = WorldEditBrush.getSchematicBrush(event.getPlayer());
        if (schematicBrush.isEmpty()) return;

        var brush = schematicBrush.get();
        if (event.getPlayer().isSneaking()) {
            if (brush.nextPaste().shiftFlip()) {
                messageSender.sendActionBar(event.getPlayer(), "§2Changed flip.");
            } else {
                messageSender.sendErrorActionBar(event.getPlayer(), "Flip is not shiftable.");
            }
        } else {
            if (brush.nextPaste().shiftRotation()) {
                messageSender.sendActionBar(event.getPlayer(), "§2Changed rotation.");
            } else {
                messageSender.sendErrorActionBar(event.getPlayer(), "Rotation is not shiftable.");
            }
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemDrop(PlayerSwapHandItemsEvent event) {
        var material = event.getOffHandItem();
        if (material == null) return;
        var schematicBrush = WorldEditBrush.getSchematicBrush(event.getPlayer(), material.getType());
        if (schematicBrush.isEmpty()) return;
        if (event.getPlayer().isSneaking()) {
            if (schematicBrush.get().nextPaste().shiftOffset()) {
                messageSender.sendActionBar(event.getPlayer(), "§2Changed Offset.");
            } else {
                messageSender.sendErrorActionBar(event.getPlayer(), "Offset is not shiftable.");
            }
        } else {
            if (schematicBrush.get().nextPaste().nextSchematic()) {
                messageSender.sendActionBar(event.getPlayer(), "§2Skipped Schematic.");
            } else {
                messageSender.sendErrorActionBar(event.getPlayer(), "The set only contains 1 schematic");
            }
        }
        event.setCancelled(true);
    }
}
