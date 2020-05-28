package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.tool.brush.Brush;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushConfiguration;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;

/**
 * Command which is used to create a new brush.
 * Rewrite of old schbr command.
 */
public class BrushCommand implements TabExecutor, Randomable {
    private final JavaPlugin plugin;
    private final SchematicCache schematicCache;

    public BrushCommand(JavaPlugin plugin, SchematicCache schematicCache) {
        this.plugin = plugin;
        this.schematicCache = schematicCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can do this.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            //TODO Command explanation
            MessageSender.sendError(player, "Too few arguments.");
            return true;
        }

        Optional<BrushConfiguration> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (settings.isEmpty()) {
            return true;
        }

        Brush schematicBrush = new SchematicBrush(player, settings.get());

        boolean success = WorldEditBrushAdapter.setBrush(player, schematicBrush);
        if (success) {
            MessageSender.sendMessage(player, "Brush using "
                    + settings.get().getSchematicCount() + " schematics created.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String last = args[args.length - 1];
        if(TabUtil.isFlag(last)){
            return TabUtil.getFlagComplete(last);
        }

        return TabUtil.getBrushSyntax(last, schematicCache, plugin);
    }
}
