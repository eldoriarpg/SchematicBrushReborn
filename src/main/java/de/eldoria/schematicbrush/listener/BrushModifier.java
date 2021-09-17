package de.eldoria.schematicbrush.listener;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public class BrushModifier implements Listener {
    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(event.getPlayer());
        if (!schematicBrush.isPresent()) return;

        SchematicBrush brush = schematicBrush.get();
        if (event.getPlayer().isSneaking()) {
            brush.nextPaste().shiftFlip();
        } else {
            brush.nextPaste().shiftRotation();
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Material material = event.getItemDrop().getItemStack().getType();
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(event.getPlayer(), material);
        if (!schematicBrush.isPresent()) return;

        schematicBrush.get().nextPaste().shiftSchematic();
        event.setCancelled(true);
    }
}
