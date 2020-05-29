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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Brush to create and modify brush presets.
 */
public class SchematicPresetCommand implements TabExecutor {

    private final Plugin plugin;
    private final SchematicCache schematicCache;
    private static final String[] COMMANDS = {"savecurrent", "save", "appendSet", "removeSet", "remove", "info", "list", "descr", "help"};

    public SchematicPresetCommand(Plugin plugin, SchematicCache schematicCache) {
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

        if (args.length == 0 || "help".equalsIgnoreCase(args[0]) || "h".equalsIgnoreCase(args[0])) {
            help(player);
            return true;
        }

        String[] subcommandArgs = Arrays.copyOfRange(args, 1, args.length);

        String cmd = args[0];

        if ("savecurrent".equalsIgnoreCase(cmd) || "c".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.save")) {
                savecurrent(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("save".equalsIgnoreCase(cmd) || "s".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.save")) {
                save(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("appendset".equalsIgnoreCase(cmd) || "ab".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.modify")) {
                appendSet(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("removeset".equalsIgnoreCase(cmd) || "rb".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.modify")) {
                removeSets(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.remove")) {
                removePreset(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                presetInfo(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("list".equalsIgnoreCase(cmd) || "l".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                presetList(player);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("descr".equalsIgnoreCase(cmd) || "d".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.save")) {
                setDescription(player, subcommandArgs);
            } else {
                MessageSender.sendError(player, "You don't have the permission to do this!");
            }
        }


        return true;
    }

    private void help(Player player) {
        MessageSender.sendMessage(player,
                "This command allows you to save and modify schematic set presets." + C.NEW_LINE
                        + "§b/sbrp save§nc§r§burrent <id> §r- Save your current equiped schematic set as a preset." + C.NEW_LINE
                        + "§b/sbrp §ns§r§bave <id> <schematic sets...> §r- Save one or more schematic sets as a preset." + C.NEW_LINE
                        + "§b/sbrp §nd§r§bescr §r- Set a description for a preset." + C.NEW_LINE
                        + "§b/sbrp §na§r§bppend§ns§r§bet <id> <schematic sets...> §r- Add one or more schematic sets to a preset." + C.NEW_LINE
                        + "§b/sbrp §nr§r§bemove§ns§r§bet <id> <id> §r- Remove schematic set from a preset." + C.NEW_LINE
                        + "§b/sbrp §nr§r§bemove <id> §r- Remove a preset." + C.NEW_LINE
                        + "§b/sbrp §ni§r§bnfo <id> §r- Get a list of schmematic sets inside a preset." + C.NEW_LINE
                        + "§b/sbrp §nl§r§bist §r- Get a list of all presets with description." + C.NEW_LINE
                        + "Use the id from the info command to change or remove a schematic set."
        );
    }

    private void savecurrent(Player player, String[] args) {
        if (args.length < 1) {
            MessageSender.sendError(player, "Too few arguments. Please provide a preset name.");
            return;
        }

        if (args.length > 1) {
            MessageSender.sendError(player, "Too many arguments. Names are not allowed to have spaces.");
            return;
        }

        String name = args[0];

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);
        if (schematicBrush.isEmpty()) {
            MessageSender.sendError(player, "This tool is not a schematic brush");
        }

        SchematicBrush brush = schematicBrush.get();

        List<String> schematicSets = getSchematicSets(brush.getSettings());

        plugin.getConfig().contains("presets." + name + ".description");

        savePreset(player, name, schematicSets);
        setDescription(player, name, "none");

        MessageSender.sendMessage(player, "Preset " + name + " saved!" + C.NEW_LINE
                + "Preset contains " + schematicSets.size() + " schematic sets with "
                + brush.getSettings().getSchematicCount() + " schematics.");
    }

    private void save(Player player, String[] args) {
        if (args.length != 2) {
            MessageSender.sendError(player, "Too few arguments");
        }
        String name = args[0];

        String[] brushArgs = Arrays.copyOfRange(args, 1, args.length);

        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, brushArgs);

        if (settings.isEmpty()) {
            return;
        }

        List<String> schematicSets = getSchematicSets(settings.get());
        savePreset(player, name, schematicSets);
        setDescription(player, name, "none");

        MessageSender.sendMessage(player, "Preset " + name + " saved!" + C.NEW_LINE
                + "Preset contains " + schematicSets.size() + " schematic sets with "
                + settings.get().getSchematicCount() + " schematics.");

    }

    private void appendSet(Player player, String[] args) {
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments");
        }
        String name = args[0];

        String[] brushArgs = Arrays.copyOfRange(args, 1, args.length);

        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, brushArgs);

        if (settings.isEmpty()) {
            return;
        }

        addSchematicSets(player, name, getSchematicSets(settings.get()));

        MessageSender.sendMessage(player, "Preset " + name + " changed!" + C.NEW_LINE
                + "Added §b" + settings.get().getSchematicSets().size() + "§r schematic sets with §b"
                + settings.get().getSchematicCount() + "§r schematics.");
    }

    private void setDescription(Player player, String[] args) {
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments");
        }

        String name = args[0];

        setDescription(player, name, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        MessageSender.sendMessage(player, "Changed description of preset §b" + name + "§r!");
    }

    private void removeSets(Player player, String[] args) {
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments");
        }
        String name = args[0];

        Object[] original;
        String[] ids = Arrays.copyOfRange(args, 1, args.length);

        Optional<List<String>> optionalSchematics = getSchematicSetsFromConfig(name);
        if (optionalSchematics.isEmpty()) {
            MessageSender.sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }

        List<String> schematicSets = optionalSchematics.get();

        for (String id : ids) {
            try {
                int i = Integer.parseInt(id);
                if (i > schematicSets.size() || i < 1) {
                    MessageSender.sendError(player, "§b" + id + "§r is not a valid id.");
                    return;
                }
                schematicSets.set(i - 1, null);
            } catch (NumberFormatException e) {
                MessageSender.sendError(player, "§b" + id + "§r is not a valid id.");
                return;
            }
        }

        overrideSchematicSets(player, name, schematicSets.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        MessageSender.sendMessage(player, "Removed schematic set from preset " + name);
    }

    private void removePreset(Player player, String[] args) {
        if (args.length == 0) {
            MessageSender.sendError(player, "Too few Arguments");
            return;
        }

        String name = args[0];
        String path = "presets." + name;
        if (!plugin.getConfig().isSet(path)) {
            MessageSender.sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }
        plugin.getConfig().set(path, null);
        plugin.saveConfig();
        MessageSender.sendMessage(player, "Preset §b" + name + "§r deleted!");
    }

    private void presetInfo(Player player, String[] args) {
        if (args.length == 0) {
            MessageSender.sendError(player, "Too few Arguments");
            return;
        }

        String name = args[0];
        String path = "presets." + name;

        Optional<List<String>> schematicSetsConfig = getSchematicSetsFromConfig(name);
        if (schematicSetsConfig.isEmpty()) {
            MessageSender.sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }
        List<String> schematicSets = schematicSetsConfig.get();
        List<String> schematicSetsList = new ArrayList<>();
        for (int i = 0; i < schematicSets.size(); i++) {
            schematicSetsList.add("§b" + (i + 1) + "| §r" + schematicSets.get(i));
        }

        MessageSender.sendMessage(player, "Information about preset §b" + name + "§r" + C.NEW_LINE
                + "§bDescription:§r " + getDescription(player, name) + C.NEW_LINE
                + "§bSchematic sets (" + schematicSetsList.size() + ")§r:" + C.NEW_LINE
                + String.join(C.NEW_LINE, schematicSetsList));
    }


    private void presetList(Player player) {
        ConfigurationSection presets = plugin.getConfig().getConfigurationSection("presets");
        if (presets == null) {
            plugin.getLogger().warning("Preset section is missing!");
            return;
        }

        String presetString = presets.getKeys(false).stream()
                .map(k -> "§bID: §r" + k + C.NEW_LINE + "  §bDesc:§r " + getDescription(player, k))
                .collect(Collectors.joining("\n"));
        MessageSender.sendMessage(player, presetString);
    }


    /**
     * Saves a list of schematic sets to a preset.
     *
     * @param player     player for error handling
     * @param presetName name of preset
     * @param schematicSets  schematic sets
     */
    private void savePreset(Player player, String presetName, List<String> schematicSets) {
        String path = "presets." + presetName + ".filter";
        boolean presetPresent = plugin.getConfig().isSet(path);
        if (presetPresent) {
            MessageSender.sendError(player, "Preset §b" + presetName + "§r does already exist.");
            return;
        }

        plugin.getConfig().set(path, schematicSets);
        plugin.saveConfig();
    }

    /**
     * Add a list of schematic sets to a existing preset.
     *
     * @param player     player for error handling
     * @param presetName name of preset
     * @param schematicSets  schematic sets
     */
    private void addSchematicSets(Player player, String presetName, List<String> schematicSets) {
        String path = "presets." + presetName + ".filter";
        boolean presetPresent = plugin.getConfig().isSet(path);
        if (!presetPresent) {
            MessageSender.sendError(player, "Preset §b" + presetName + "§r does not exist.");
            return;
        }

        schematicSets.addAll(plugin.getConfig().getStringList(path));

        plugin.getConfig().set(path, schematicSets);
        plugin.saveConfig();
    }

    /**
     * Replaces the current list of schematic sets of a preset with a new list.
     *
     * @param player     player for error handling
     * @param presetName name of preset
     * @param schematicSets  schematic sets
     */
    private void overrideSchematicSets(Player player, String presetName, List<String> schematicSets) {
        String path = "presets." + presetName + ".filter";
        boolean presetPresent = plugin.getConfig().isSet(path);
        if (!presetPresent) {
            MessageSender.sendError(player, "Preset §b" + presetName + "§r does not exist.");
            return;
        }

        plugin.getConfig().set(path, schematicSets);
        plugin.saveConfig();
    }

    private List<String> getSchematicSets(BrushSettings brush) {
        return brush.getSchematicSets().stream()
                .map(SchematicSet::getArguments)
                .collect(Collectors.toList());
    }

    /**
     * Loads the schematic sets of a preset from config.
     *
     * @param presetName name of preset
     * @return optional list of schematic sets when the preset is present in config
     */
    private Optional<List<String>> getSchematicSetsFromConfig(String presetName) {
        String path = "presets." + presetName + ".filter";
        if (plugin.getConfig().contains(path)) {
            return Optional.of(plugin.getConfig().getStringList(path));
        }
        return Optional.empty();
    }

    private void setDescription(Player player, String name, String descr) {
        String path = "presets." + name;
        boolean presetPresent = plugin.getConfig().isSet(path);
        if (!presetPresent) {
            MessageSender.sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }
        plugin.getConfig().set(path + ".description", descr);
        plugin.saveConfig();
    }

    private String getDescription(Player player, String name) {
        String path = "presets." + name + ".description";
        boolean presetPresent = plugin.getConfig().isSet(path);
        if (!presetPresent) {
            MessageSender.sendError(player, "Preset §b" + name + "§r does not exist.");
            return null;
        }
        return plugin.getConfig().getString(path);
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
            return null;
        }

        if ("savecurrent".equalsIgnoreCase(cmd) || "c".equalsIgnoreCase(cmd)) {
            ConfigurationSection presets = plugin.getConfig().getConfigurationSection("presets");
            if (presetExists(last)) {
                return List.of("This name is already in use!");
            }
            return List.of("<name of preset>");
        }

        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)
                || "info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, plugin, 50);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, plugin, 50);
            }
        }

        if ("save".equalsIgnoreCase(cmd) || "s".equalsIgnoreCase(cmd)) {
            if (args.length == 2) {
                if (presetExists(last)) {
                    return List.of("This name is already in use!");
                }
                return List.of("<name of preset>");
            }
            return TabUtil.getSchematicSetSyntax(last, schematicCache, plugin);
        }

        if ("appendSet".equalsIgnoreCase(cmd) || "ab".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, plugin, 50);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, plugin, 50);
            }

            return TabUtil.getSchematicSetSyntax(last, schematicCache, plugin);
        }

        if ("removeSet".equalsIgnoreCase(cmd) || "rb".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, plugin, 50);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, plugin, 50);
            }
            if (args.length == 3) {
                return List.of("<id of schematic set>");
            }
        }

        if ("list".equalsIgnoreCase(cmd) || "l".equalsIgnoreCase(cmd)) {
            return Collections.emptyList();
        }

        if ("descr".equalsIgnoreCase(cmd) || "d".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, plugin, 50);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, plugin, 50);
            }
            return List.of("<description>");
        }

        if (args.length == 1) {
            return TabUtil.startingWithInArray(cmd, COMMANDS).collect(Collectors.toList());
        }
        return null;
    }

    private boolean presetExists(String name) {
        ConfigurationSection presets = plugin.getConfig().getConfigurationSection("presets");
        if (presets != null) {
            Set<String> keys = presets.getKeys(false);
            return keys.stream().anyMatch(s -> s.equalsIgnoreCase(name));
        }
        return false;
    }
}
