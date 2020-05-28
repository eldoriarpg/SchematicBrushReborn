package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.WorldEdit;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushConfiguration;
import de.eldoria.schematicbrush.brush.config.SubBrush;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

/**
 * Command to modify a current used brush.
 */
public class BrushModifyCommand implements CommandExecutor, Randomable {
    private final JavaPlugin plugin;
    private final SchematicCache schematicCache;

    public BrushModifyCommand(JavaPlugin plugin, SchematicCache schematicCache) {
        this.plugin = plugin;
        this.schematicCache = schematicCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Commands:
        // Append a brush to current brush - sbrmod append <brushes...>
        // Remove a brush from current brush - sbrmod remove <id>
        // Replace a brush on current brush - sbrmod edit <id> <newBrush>
        // Get list of brushes in current brush - sbrmod info

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can do this.");
            return true;
        }

        Player player = (Player) sender;

        // Display command help
        if (args.length == 0) {
            help(player);
            return true;
        }

        String[] subcommandArgs = Arrays.copyOfRange(args, 1, args.length);

        String cmd = args[0];
        if ("append".equalsIgnoreCase(cmd) || "a".equalsIgnoreCase(cmd)) {
            appendBrush(player, subcommandArgs);
            return true;
        }
        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)) {
            removeBrush(player, subcommandArgs);
            return true;
        }
        if ("edit".equalsIgnoreCase(cmd) || "e".equalsIgnoreCase(cmd)) {
            editBrush(player, subcommandArgs);
            return true;
        }
        if ("info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            brushInfo(player);
            return true;
        }
        return true;
    }

    private boolean appendBrush(Player player, String[] args) {
        Optional<BrushConfiguration> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (settings.isEmpty()) {
            return true;
        }

        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (schematicBrush.isEmpty()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return true;
        }


        SchematicBrush combinedBrush = schematicBrush.get().combineBrush(settings.get());
        boolean success = WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(settings.get()));
        if (success) {
            MessageSender.sendMessage(player, "Brush appended. Using "
                    + combinedBrush.getSettings().getSchematicCount() + " schematics.");
        }
        return false;
    }

    private void removeBrush(Player player, String[] args) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (schematicBrush.isEmpty()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return;
        }
        if (args.length == 0) {
            MessageSender.sendError(player, "Too few arguments.");
            return;
        }


        int id;
        try {
            id = Integer.parseInt(args[0]);
            if (id < 1 || id < schematicBrush.get().getSettings().getBrushes().size()) {
                MessageSender.sendError(player, "Invalid brush id.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid brush id.");
            return;
        }

        List<SubBrush> brushes = schematicBrush.get().getSettings().getBrushes();
    }

    private void editBrush(Player player, String[] args) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (schematicBrush.isEmpty()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments.");
            return;
        }


        Optional<BrushConfiguration> brushConfiguration = BrushSettingsParser
                .parseBrush(player, plugin, schematicCache, Arrays.copyOfRange(args, 1, args.length));


        if (brushConfiguration.isPresent()) {
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
            if (id < 1 || id < schematicBrush.get().getSettings().getBrushes().size()) {
                MessageSender.sendError(player, "Invalid brush id.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid brush id.");
            return;
        }

        List<SubBrush> brushes = schematicBrush.get().getSettings().getBrushes();
        brushes.remove(id - 1);
        WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(brushConfiguration.get()));
    }

    private void brushInfo(Player player) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (schematicBrush.isEmpty()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return;
        }
        BrushConfiguration settings = schematicBrush.get().getSettings();
        List<SubBrush> brushes = settings.getBrushes();

        List<String> brushStrings = new ArrayList<>();
        for (int i = 0; i < brushes.size(); i++) {
            String arguments = brushes.get(i).getArguments();
            brushStrings.add((i + 1) + "| " + arguments);
        }

        String brushList = brushStrings.stream().collect(Collectors.joining(lineSeparator()));
        MessageSender.sendMessage(player,
                "Total schematics: " + settings.getSchematicCount() + lineSeparator()
                        + "Placement: " + settings.getPlacement().toString() + lineSeparator()
                        + "Y-Offset: " + settings.getYOffset() + lineSeparator()
                        + "Paste air: " + settings.isIncludeAir() + lineSeparator()
                        + "Replace air only: " + settings.isReplaceAirOnly() + lineSeparator()
                        + "Brushes:" + lineSeparator()
                        + brushList);
    }

    private void help(Player player) {
        MessageSender.sendMessage(player,
                "This command allows you to modify a current used brush." + lineSeparator()
                        + "/sbrm append <brushes...> - Add one or more brushes to your brush." + lineSeparator()
                        + "/sbrm remove <id> - Remove a brush." + lineSeparator()
                        + "/sbrm edit <id> <brush> - Replace a brush with another brush." + lineSeparator()
                        + "/sbrm info - Get a list of all brushes your brush contains." + lineSeparator()
                        + "Use the id from the info command to change or remove a brush."
        );
    }
}
