/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 */

package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.MessageType;
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
            brush.nextPaste().shiftFlip();
            messageSender.send(MessageChannel.ACTION_BAR, MessageType.NORMAL, event.getPlayer(), "§2Changed flip.");
        } else {
            brush.nextPaste().shiftRotation();
            messageSender.send(MessageChannel.ACTION_BAR, MessageType.NORMAL, event.getPlayer(), "§2Changed rotation.");
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
        messageSender.send(MessageChannel.ACTION_BAR, MessageType.NORMAL, event.getPlayer(), "§2Changed Offset.");
            schematicBrush.get().nextPaste().shiftOffset();
        } else {
        messageSender.send(MessageChannel.ACTION_BAR, MessageType.NORMAL, event.getPlayer(), "§2Skipped Schematic.");
            schematicBrush.get().nextPaste().shiftSchematic();
        }
        event.setCancelled(true);
    }
}
