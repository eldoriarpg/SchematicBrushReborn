package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.util.HandSide;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.FakeWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.Map;

public class RenderService implements Runnable {


    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);
            LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(bukkitPlayer);
            BrushTool brushTool;
            try {
                brushTool = localSession.getBrushTool(bukkitPlayer.getItemInHand(HandSide.MAIN_HAND).getType());
            } catch (InvalidToolBindException e) {
                e.printStackTrace();
                return;
            }

            if (!(brushTool.getBrush() instanceof SchematicBrush)) {
                return;
            }
            SchematicBrush brush = (SchematicBrush) brushTool.getBrush();
            FakeWorld world = brush.pasteFake();
            Map<Location, BlockData> changes = world.changes();
            for (Map.Entry<Location, BlockData> entry : changes.entrySet()) {
                player.sendBlockChange(entry.getKey(), entry.getValue());
            }
        }
    }
}
