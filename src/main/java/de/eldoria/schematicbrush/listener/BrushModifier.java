package de.eldoria.schematicbrush.listener;

import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BrushModifier implements Listener {
    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;
        var schematicBrush = WorldEditBrush.getSchematicBrush(event.getPlayer());
        if (schematicBrush.isEmpty()) return;

        var brush = schematicBrush.get();
        if (event.getPlayer().isSneaking()) {
            brush.nextPaste().shiftFlip();
        } else {
            brush.nextPaste().shiftRotation();
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        var material = event.getItemDrop().getItemStack().getType();
        var schematicBrush = WorldEditBrush.getSchematicBrush(event.getPlayer(), material);
        if (schematicBrush.isEmpty()) return;

        schematicBrush.get().nextPaste().shiftSchematic();
        event.setCancelled(true);
    }
}
