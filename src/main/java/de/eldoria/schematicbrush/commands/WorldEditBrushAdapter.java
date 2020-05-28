package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.extension.platform.Actor;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@UtilityClass
public class WorldEditBrushAdapter {
    private final WorldEdit WORLD_EDIT = WorldEdit.getInstance();

    public Optional<SchematicBrush> getSchematicBrush(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        try {
            BrushTool brushTool = getLocalSession(player).getBrushTool(BukkitAdapter.asItemType(itemInMainHand.getType()));
            if (brushTool.getBrush() != null && brushTool.getBrush() instanceof SchematicBrush) {
                return Optional.of((SchematicBrush) brushTool.getBrush());
            }
        } catch (InvalidToolBindException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public boolean setBrush(Player player, Brush brush) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        try {
            getLocalSession(player).getBrushTool(BukkitAdapter.asItemType(itemInMainHand.getType())).setBrush(brush, "schematicbrush.brush.use");
        } catch (InvalidToolBindException e) {
            MessageSender.sendError(player, e.getMessage());
            return false;
        }
        return true;
    }

    private LocalSession getLocalSession(Player player) {
        Actor actor = BukkitAdapter.adapt(player);

        return WORLD_EDIT.getSessionManager().get(actor);
    }
}
