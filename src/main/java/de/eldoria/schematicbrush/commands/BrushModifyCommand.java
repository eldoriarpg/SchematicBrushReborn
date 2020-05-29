package de.eldoria.schematicbrush.commands;

import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushConfiguration;
import de.eldoria.schematicbrush.brush.config.SubBrush;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.MessageSender;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Command to modify a current used brush.
 */
public class BrushModifyCommand implements TabExecutor, Randomable {
    private final JavaPlugin plugin;
    private final SchematicCache schematicCache;
    private static final String[] COMMANDS = {"append", "remove", "edit", "info", "reload", "help"};

    public BrushModifyCommand(JavaPlugin plugin, SchematicCache schematicCache) {
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

        // Display command help
        if (args.length == 0 || "help".equalsIgnoreCase(args[0]) || "h".equalsIgnoreCase(args[0])) {
            help(player);
            return true;
        }

        String[] subcommandArgs = Arrays.copyOfRange(args, 1, args.length);

        String cmd = args[0];

        if ("append".equalsIgnoreCase(cmd) || "a".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                appendBrush(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
            return true;
        }

        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                removeBrush(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
            return true;
        }

        if ("edit".equalsIgnoreCase(cmd) || "e".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                editBrush(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
            return true;
        }

        if ("reload".equalsIgnoreCase(cmd) || "rel".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                reload(player);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
            return true;
        }
        if ("info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                brushInfo(player);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
            return true;
        }
        return true;
    }

    private void appendBrush(Player player, String[] args) {
        Optional<BrushConfiguration> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (settings.isEmpty()) {
            return;
        }

        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (schematicBrush.isEmpty()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return;
        }


        SchematicBrush combinedBrush = schematicBrush.get().combineBrush(settings.get());
        boolean success = WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(settings.get()));
        if (success) {
            MessageSender.sendMessage(player, "Brush appended. Using "
                    + combinedBrush.getSettings().getSchematicCount() + " schematics.");
        }
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
            if (id < 1 || id > schematicBrush.get().getSettings().getBrushes().size()) {
                MessageSender.sendError(player, "Invalid brush id.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid brush id.");
            return;
        }

        List<SubBrush> brushes = schematicBrush.get().getSettings().getBrushes();
        SubBrush remove = brushes.remove(id - 1);

        MessageSender.sendMessage(player, "Brush " + remove.getArguments() + " removed!");
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


        if (brushConfiguration.isEmpty()) {
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
            if (id < 1 || id > schematicBrush.get().getSettings().getBrushes().size()) {
                MessageSender.sendError(player, "Invalid brush id.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid brush id.");
            return;
        }

        List<SubBrush> brushes = schematicBrush.get().getSettings().getBrushes();
        SubBrush remove = brushes.remove(id - 1);
        WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(brushConfiguration.get()));
        MessageSender.sendMessage(player, "Brush " + remove.getArguments() + "changed to "
                + brushConfiguration.get().getBrushes().get(0).getArguments() + ".");
    }

    private void reload(Player player) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (schematicBrush.isEmpty()) {
            MessageSender.sendMessage(player, "This is not a schematic brush!");
            return;
        }

        BrushConfiguration oldSettings = schematicBrush.get().getSettings();
        Optional<BrushConfiguration.BrushConfigurationBuilder> configurationBuilder = BrushSettingsParser.buildBrushes(player,
                oldSettings.getBrushes().stream().map(SubBrush::getArguments).collect(Collectors.toList()),
                plugin, schematicCache);

        if (configurationBuilder.isEmpty()) {
            return;
        }

        BrushConfiguration.BrushConfigurationBuilder builder = configurationBuilder.get();

        BrushConfiguration configuration = builder.includeAir(oldSettings.isIncludeAir())
                .replaceAirOnly(oldSettings.isReplaceAirOnly())
                .withPlacementType(oldSettings.getPlacement())
                .withYOffset(oldSettings.getYOffset())
                .build();

        int oldCount = oldSettings.getSchematicCount();
        int newcount = configuration.getSchematicCount();
        int addedSchematics = newcount - oldCount;
        WorldEditBrushAdapter.setBrush(player, new SchematicBrush(player, configuration));
        if (addedSchematics != 0) {
            MessageSender.sendMessage(player, "Brush reloaded. Added " + addedSchematics + " schematics" + C.NEW_LINE
                    + "Brush is now using " + newcount + " schematics.");
        } else {
            MessageSender.sendMessage(player, "No new schematics were found." +
                    "Maybe you have to reload the schematics first. Use /sbra reloadschematics");
        }
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

        String brushList = brushStrings.stream().collect(Collectors.joining(C.NEW_LINE));
        MessageSender.sendMessage(player,
                "Total schematics: " + settings.getSchematicCount() + C.NEW_LINE
                        + "Placement: " + settings.getPlacement().toString() + C.NEW_LINE
                        + "Y-Offset: " + settings.getYOffset() + C.NEW_LINE
                        + "Paste air: " + settings.isIncludeAir() + C.NEW_LINE
                        + "Replace air only: " + settings.isReplaceAirOnly() + C.NEW_LINE
                        + "Brushes:" + C.NEW_LINE
                        + brushList);
    }

    private void help(Player player) {
        MessageSender.sendMessage(player,
                "This command allows you to modify a current used brush." + C.NEW_LINE
                        + "/sbrm append <brushes...> - Add one or more brushes to your brush." + C.NEW_LINE
                        + "/sbrm remove <id> - Remove a brush." + C.NEW_LINE
                        + "/sbrm edit <id> <brush> - Replace a brush with another brush." + C.NEW_LINE
                        + "/sbrm reload - Reload matching schematics, if new schematics were recently added."
                        + "You may want to use /sbra reloadschematics first." + C.NEW_LINE
                        + "/sbrm info - Get a list of all brushes your brush contains." + C.NEW_LINE
                        + "Use the id from the info command to change or remove a brush."
        );
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        if (args[0].isEmpty()) {
            return Arrays.asList(COMMANDS);
        }

        String last = args[args.length - 1];

        String cmd = args[0];
        if ("help".equalsIgnoreCase(cmd) || "h".equalsIgnoreCase(cmd)) {
            return Collections.emptyList();
        }

        if ("append".equalsIgnoreCase(cmd) || "a".equalsIgnoreCase(cmd)) {
            return TabUtil.getBrushSyntax(last, schematicCache, plugin);
        }

        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)) {
            return List.of("<brush id>");
        }

        if ("reload".equalsIgnoreCase(cmd) || "rel".equalsIgnoreCase(cmd)) {
            return Collections.emptyList();
        }

        if ("edit".equalsIgnoreCase(cmd) || "e".equalsIgnoreCase(cmd)) {
            if (args.length == 1) {
                return List.of("<brush id> <brush>");
            }
            return TabUtil.getBrushSyntax(last, schematicCache, plugin);
        }

        if ("info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return TabUtil.startingWithInArray(cmd, COMMANDS).collect(Collectors.toList());
        }
        return null;
    }
}
