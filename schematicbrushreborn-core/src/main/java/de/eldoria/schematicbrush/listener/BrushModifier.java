/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
