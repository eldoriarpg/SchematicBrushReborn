package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.extension.platform.Actor;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.brush.BrushSettings;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class BrushCommand implements CommandExecutor, Randomable {
    private final JavaPlugin plugin;
    private final WorldEdit we;
    private final SchematicCache schematicCache;


    public BrushCommand(JavaPlugin plugin, SchematicCache schematicCache) {
        this.plugin = plugin;
        this.we = WorldEdit.getInstance();
        this.schematicCache = schematicCache;
    }

    //TODO: append brushes to brush. aka temp presets
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can do this.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            MessageSender.sendError(player, "Too few arguments.");
            return true;
        }

        Actor actor = BukkitAdapter.adapt(player);

        LocalSession localSession = we.getSessionManager().get(actor);
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (settings.isEmpty()) {
            return true;
        }

        SchematicBrush schematicBrush = new SchematicBrush(player, settings.get());

        try {
            BrushTool brushTool = localSession.getBrushTool(BukkitAdapter.asItemType(itemInMainHand.getType()));
            brushTool.setBrush(schematicBrush, "schematicbrush.brush.use");
            player.sendMessage("Schematic brush set.");
        } catch (InvalidToolBindException e) {
            MessageSender.sendError(player, e.getMessage());
            return true;
        }


        return true;
    }
}
