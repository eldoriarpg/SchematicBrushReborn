package de.eldoria.schematicbrush.commands;

import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
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
        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (!settings.isPresent()) {
            return;
        }

        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (!schematicBrush.isPresent()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return;
        }


        SchematicBrush combinedBrush = schematicBrush.get().combineBrush(settings.get());
        boolean success = WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(settings.get()));
        if (success) {
            MessageSender.sendMessage(player, "Schematic set appended. Using §b"
                    + combinedBrush.getSettings().getSchematicCount() + "§r schematics.");
        }
    }

    private void removeBrush(Player player, String[] args) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (!schematicBrush.isPresent()) {
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
            if (id < 1 || id > schematicBrush.get().getSettings().getSchematicSets().size()) {
                MessageSender.sendError(player, "Invalid set id.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid set id.");
            return;
        }

        List<SchematicSet> schematicSets = schematicBrush.get().getSettings().getSchematicSets();
        SchematicSet remove = schematicSets.remove(id - 1);

        MessageSender.sendMessage(player, "Set §b" + remove.getArguments() + "§r removed!");
    }

    private void editBrush(Player player, String[] args) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (!schematicBrush.isPresent()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return;
        }
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments.");
            return;
        }


        Optional<BrushSettings> brushConfiguration = BrushSettingsParser
                .parseBrush(player, plugin, schematicCache, Arrays.copyOfRange(args, 1, args.length));


        if (!brushConfiguration.isPresent()) {
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
            if (id < 1 || id > schematicBrush.get().getSettings().getSchematicSets().size()) {
                MessageSender.sendError(player, "Invalid set id.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid set id.");
            return;
        }

        List<SchematicSet> schematicSets = schematicBrush.get().getSettings().getSchematicSets();
        SchematicSet remove = schematicSets.remove(id - 1);
        WorldEditBrushAdapter.setBrush(player, schematicBrush.get().combineBrush(brushConfiguration.get()));
        MessageSender.sendMessage(player, "Set §b" + remove.getArguments() + "§r changed to §b"
                + brushConfiguration.get().getSchematicSets().get(0).getArguments() + "§r.");
    }

    private void reload(Player player) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (!schematicBrush.isPresent()) {
            MessageSender.sendMessage(player, "This is not a schematic brush!");
            return;
        }

        BrushSettings oldSettings = schematicBrush.get().getSettings();
        Optional<BrushSettings.BrushSettingsBuilder> configurationBuilder = BrushSettingsParser.buildBrushes(player,
                oldSettings.getSchematicSets().stream().map(SchematicSet::getArguments).collect(Collectors.toList()),
                plugin, schematicCache);

        if (!configurationBuilder.isPresent()) {
            return;
        }

        BrushSettings.BrushSettingsBuilder builder = configurationBuilder.get();

        BrushSettings configuration = builder.includeAir(oldSettings.isIncludeAir())
                .replaceAll(oldSettings.isReplaceAll())
                .withPlacementType(oldSettings.getPlacement())
                .withYOffset(oldSettings.getYOffset())
                .build();

        int oldCount = oldSettings.getSchematicCount();
        int newcount = configuration.getSchematicCount();
        int addedSchematics = newcount - oldCount;
        WorldEditBrushAdapter.setBrush(player, new SchematicBrush(player, configuration));
        if (addedSchematics > 0) {
            MessageSender.sendMessage(player, "Brush reloaded. Added §b" + addedSchematics + "§r schematics" + C.NEW_LINE
                    + "Brush is now using §b" + newcount + "§r schematics.");
        } else if (addedSchematics < 0) {
            MessageSender.sendMessage(player, "Brush reloaded. Removed §b" + addedSchematics + "§r schematics" + C.NEW_LINE
                    + "Brush is now using §b" + newcount + "§r schematics.");

        } else {
            MessageSender.sendMessage(player, "§cNo new schematics were found.§r " +
                    "Maybe you have to reload the schematics first. Use §b/sbra reloadschematics§r");
        }
    }

    private void brushInfo(Player player) {
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (!schematicBrush.isPresent()) {
            MessageSender.sendError(player, "This is not a schematic brush.");
            return;
        }
        BrushSettings settings = schematicBrush.get().getSettings();
        List<SchematicSet> schematicSets = settings.getSchematicSets();

        List<String> schematicSetStrings = new ArrayList<>();
        for (int i = 0; i < schematicSets.size(); i++) {
            String arguments = schematicSets.get(i).getArguments();
            schematicSetStrings.add("§b" + (i + 1) + "|§r " + arguments);
        }

        String schematicSetList = String.join(C.NEW_LINE, schematicSetStrings);
        MessageSender.sendMessage(player,
                "§bTotal schematics:§r " + settings.getSchematicCount() + C.NEW_LINE
                        + "§bPlacement:§r " + settings.getPlacement().toString() + C.NEW_LINE
                        + "§bY-Offset:§r " + settings.getYOffset() + C.NEW_LINE
                        + "§bPaste air:§r " + settings.isIncludeAir() + C.NEW_LINE
                        + "§bReplace all blocks:§r " + settings.isReplaceAll() + C.NEW_LINE
                        + "§bSchematic sets:§r" + C.NEW_LINE
                        + schematicSetList);
    }

    private void help(Player player) {
        MessageSender.sendMessage(player,
                "This command allows you to modify a current used brush." + C.NEW_LINE
                        + "§b/sbrm §na§r§bppend <schematic sets...>§r - Add one or more schematic sets to your brush." + C.NEW_LINE
                        + "§b/sbrm §nr§r§bemove <id>§r - Remove a schematic set." + C.NEW_LINE
                        + "§b/sbrm §ne§r§bdit <id> <brush>§r - Replace a brush with another brush." + C.NEW_LINE
                        + "§b/sbrm §nrel§r§boad §r- Reload matching schematics, if new schematics were recently added."
                        + "You may want to use §b/sbra reloadschematics§r first." + C.NEW_LINE
                        + "§b/sbrm §ni§r§bnfo §r- Get all settings and a list of all schematic sets your brush uses." + C.NEW_LINE
                        + "Use the id from the info command to change or remove a schematic set."
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
            return TabUtil.getSchematicSetSyntax(last, schematicCache, plugin);
        }

        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)) {
            return Collections.singletonList("<schematic set id>");
        }

        if ("reload".equalsIgnoreCase(cmd) || "rel".equalsIgnoreCase(cmd)) {
            return Collections.emptyList();
        }

        if ("edit".equalsIgnoreCase(cmd) || "e".equalsIgnoreCase(cmd)) {
            if (args.length == 1) {
                return Collections.singletonList("<schematic set id> <schematic set>");
            }
            if (args.length == 2) {
                return Collections.singletonList("<schematic set>");
            }
            return TabUtil.getSchematicSetSyntax(last, schematicCache, plugin);
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
