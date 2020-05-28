package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.command.tool.BrushTool;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.extension.platform.Actor;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushConfiguration;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class BrushModifyCommand implements CommandExecutor, Randomable {
    private final JavaPlugin plugin;
    private final WorldEdit we;
    private final SchematicCache schematicCache;

    public BrushModifyCommand(JavaPlugin plugin, SchematicCache schematicCache) {
        this.plugin = plugin;
        this.we = WorldEdit.getInstance();
        this.schematicCache = schematicCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Commands:
        // Append a brush to current brush - sbrmod append <brushes...>
        // Remove a brush from current brush - sbrmod remove <brush>
        // Replace a brush on current brush - sbrmod alter <oldBrush> <newBrush>
        // Get list of brushes in current brush - sbrmod info

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

        Optional<BrushConfiguration> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (settings.isEmpty()) {
            return true;
        }

        try {
            BrushTool brushTool = localSession.getBrushTool(BukkitAdapter.asItemType(itemInMainHand.getType()));
            if (brushTool.getBrush() != null && brushTool.getBrush() instanceof SchematicBrush) {
                SchematicBrush currentBrush = (SchematicBrush) brushTool.getBrush();
                SchematicBrush combinedBrush = currentBrush.combineBrush(settings.get());
                brushTool.setBrush(combinedBrush, "schematicbrush.brush.use");
                MessageSender.sendMessage(player, "Brush appended. Using "
                        + combinedBrush.getSettings().getSchematicCount() + " schematics.");
            } else {
                MessageSender.sendError(player, "This is not a schematic brush.");
            }
        } catch (InvalidToolBindException e) {
            MessageSender.sendError(player, e.getMessage());
        }
        return true;
    }
}
