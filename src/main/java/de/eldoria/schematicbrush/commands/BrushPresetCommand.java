package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.extension.platform.Actor;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.SubBrush;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class BrushPresetCommand implements CommandExecutor {

    private final WorldEdit we;
    private final Plugin plugin;
    private final SchematicCache schematicCache;

    public BrushPresetCommand(Plugin plugin, SchematicCache schematicCache) {
        this.plugin = plugin;
        this.schematicCache = schematicCache;
        this.we = WorldEdit.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Commands:
        // Save the current applied brush: /sbrpre savecurrent <id>
        // Save brush: /sbrpre current <id> <brushstrings>
        // Append a brush to preset: /sbrpre append <id> <brushstrings>
        // Append current brush to preset: /sbrpre append <id>
        // Remove a brush from preset: /sbrpre remove <id> <brushstrings>
        // Information about a preset: /sbrpre info <id>
        // List all presets with description: /sbrpre list
        // List all presets with description: /sbrpre descr <id> <description>

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can do this.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            MessageSender.sendError(player, "Too few arguments.");
            return true;
        }

        savecurrent(player, args);
        return true;
    }

    private void savecurrent(Player player, String[] args) {
        Actor actor = BukkitAdapter.adapt(player);

        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments. Please provide a preset name.");
            return;
        }

        if (args.length > 2) {
            MessageSender.sendError(player, "Too many arguments. Names are not allowed to have spaces.");
            return;
        }

        String name = args[1];

        LocalSession localSession = we.getSessionManager().get(actor);
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        BrushTool brushTool;
        try {
            brushTool = localSession.getBrushTool(BukkitAdapter.asItemType(itemInMainHand.getType()));
        } catch (InvalidToolBindException e) {
            MessageSender.sendError(player, e.getMessage());
            return;
        }

        if (brushTool == null) {
            MessageSender.sendError(player, "This tool is not a brush");
            return;
        }

        if (!(brushTool.getBrush() instanceof SchematicBrush)) {
            MessageSender.sendError(player, "This tool is not a schematic brush");
            return;
        }

        SchematicBrush brush = (SchematicBrush) brushTool.getBrush();

        List<String> collect = brush.getSettings().getBrushes().stream()
                .map(SubBrush::getArguments)
                .collect(Collectors.toList());

        plugin.getConfig().contains("presets." + name + ".description");
    }

    private void saveBrushesToPreset(Player player, String presetName, List<String> brushArgs, boolean append) {
        String path = "presets." + presetName + ".filter";
        boolean brushesPresent = plugin.getConfig().contains(path);
        if (brushesPresent && !append) {
            MessageSender.sendError(player, "Preset " + presetName + " does already exist.");
            return;
        }
        if (brushesPresent) {
            brushArgs.addAll(plugin.getConfig().getStringList(path));
        }

        plugin.getConfig().set(path, brushArgs);

    }
}
