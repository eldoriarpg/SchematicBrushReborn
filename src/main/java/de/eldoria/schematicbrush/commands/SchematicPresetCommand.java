package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.utils.ArrayUtil;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.commands.util.WorldEditBrushAdapter;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.sections.Preset;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Brush to create and modify brush presets.
 */
public class SchematicPresetCommand extends EldoCommand {
    private static final String[] COMMANDS = {"savecurrent", "save", "appendSet", "removeSet", "remove", "info", "list", "descr", "help"};
    private final SchematicCache schematicCache;
    private final Config config;

    public SchematicPresetCommand(Plugin plugin, SchematicCache schematicCache, Config config) {
        super(plugin);
        this.schematicCache = schematicCache;
        this.config = config;
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
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("save".equalsIgnoreCase(cmd) || "s".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.save")) {
                save(player, subcommandArgs);
            } else {
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("appendset".equalsIgnoreCase(cmd) || "ab".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.modify")) {
                appendSet(player, subcommandArgs);
            } else {
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("removeset".equalsIgnoreCase(cmd) || "rb".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.modify")) {
                removeSets(player, subcommandArgs);
            } else {
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.remove")) {
                removePreset(player, subcommandArgs);
            } else {
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                presetInfo(player, subcommandArgs);
            } else {
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("list".equalsIgnoreCase(cmd) || "l".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.brush.use")) {
                presetList(player);
            } else {
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }

        if ("descr".equalsIgnoreCase(cmd) || "d".equalsIgnoreCase(cmd)) {
            if (player.hasPermission("schematicbrush.preset.save")) {
                setDescription(player, subcommandArgs);
            } else {
                messageSender().sendError(player, "You don't have the permission to do this!");
            }
        }


        return true;
    }

    private void help(Player player) {
        messageSender().sendMessage(player,
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
            messageSender().sendError(player, "Too few arguments. Please provide a preset name.");
            return;
        }

        if (args.length > 1) {
            messageSender().sendError(player, "Too many arguments. Names are not allowed to have spaces.");
            return;
        }

        String name = args[0];

        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);

        if (!schematicBrush.isPresent()) {
            messageSender().sendError(player, "This tool is not a schematic brush");
            return;
        }

        SchematicBrush brush = schematicBrush.get();

        List<String> schematicSets = getSchematicSets(brush.getSettings());

        config.presetExists(name);

        if (!savePreset(player, name, schematicSets)) return;

        messageSender().sendMessage(player, "Preset " + name + " saved!" + C.NEW_LINE
                + "Preset contains " + schematicSets.size() + " schematic sets with "
                + brush.getSettings().getSchematicCount() + " schematics.");
    }

    private void save(Player player, String[] args) {
        if (args.length != 2) {
            messageSender().sendError(player, "Too few arguments");
            return;
        }
        String name = args[0];

        String[] brushArgs = Arrays.copyOfRange(args, 1, args.length);

        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, config, schematicCache, brushArgs);

        if (!settings.isPresent()) {
            return;
        }

        List<String> schematicSets = getSchematicSets(settings.get());

        if (!savePreset(player, name, schematicSets)) return;

        messageSender().sendMessage(player, "Preset " + name + " saved!" + C.NEW_LINE
                + "Preset contains " + schematicSets.size() + " schematic sets with "
                + settings.get().getSchematicCount() + " schematics.");
    }

    private void appendSet(Player player, String[] args) {
        if (args.length < 2) {
            messageSender().sendError(player, "Too few arguments");
        }
        String name = args[0];

        String[] brushArgs = Arrays.copyOfRange(args, 1, args.length);

        Optional<BrushSettings> settings = BrushSettingsParser.parseBrush(player, config, schematicCache, brushArgs);


        if (!settings.isPresent()) {
            return;
        }

        Optional<Preset> preset = config.getPreset(name);
        if (!preset.isPresent()) {
            messageSender().sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }

        preset.get().getFilter().addAll(getSchematicSets(settings.get()));
        config.save();

        messageSender().sendMessage(player, "Preset " + name + " changed!" + C.NEW_LINE
                + "Added §b" + settings.get().getSchematicSets().size() + "§r schematic sets with §b"
                + settings.get().getSchematicCount() + "§r schematics.");
    }

    private void setDescription(Player player, String[] args) {
        if (args.length < 2) {
            messageSender().sendError(player, "Too few arguments");
        }

        String name = args[0];

        Optional<Preset> preset = config.getPreset(name);
        if (!preset.isPresent()) {
            messageSender().sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }
        preset.get().setDescription(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
        messageSender().sendMessage(player, "Changed description of preset §b" + name + "§r!");
        config.save();

    }

    private void removeSets(Player player, String[] args) {
        if (args.length < 2) {
            messageSender().sendError(player, "Too few arguments");
        }
        String name = args[0];

        String[] ids = Arrays.copyOfRange(args, 1, args.length);

        Optional<Preset> preset = config.getPreset(name);

        if (!preset.isPresent()) {
            messageSender().sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }

        List<String> schematicSets = preset.get().getFilter();

        for (String id : ids) {
            try {
                int i = Integer.parseInt(id);
                if (i > schematicSets.size() || i < 1) {
                    messageSender().sendError(player, "§b" + id + "§r is not a valid id.");
                    return;
                }
                schematicSets.set(i - 1, null);
            } catch (NumberFormatException e) {
                messageSender().sendError(player, "§b" + id + "§r is not a valid id.");
                return;
            }
        }
        preset.get().setFilter(schematicSets.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        config.save();
        messageSender().sendMessage(player, "Removed schematic set from preset " + name);
    }

    private void removePreset(Player player, String[] args) {
        if (args.length == 0) {
            messageSender().sendError(player, "Too few Arguments");
            return;
        }

        String name = args[0];
        if (!config.removePreset(name)) {
            messageSender().sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }
        messageSender().sendMessage(player, "Preset §b" + name + "§r deleted!");
    }

    private void presetInfo(Player player, String[] args) {
        if (args.length == 0) {
            messageSender().sendError(player, "Too few Arguments");
            return;
        }

        String name = args[0];

        Optional<Preset> optPreset = config.getPreset(name);

        if (!optPreset.isPresent()) {
            messageSender().sendError(player, "Preset §b" + name + "§r does not exist.");
            return;
        }
        Preset preset = optPreset.get();
        List<String> schematicSets = preset.getFilter();
        List<String> schematicSetsList = new ArrayList<>();
        for (int i = 0; i < schematicSets.size(); i++) {
            schematicSetsList.add("§b" + (i + 1) + "| §r" + schematicSets.get(i));
        }

        messageSender().sendMessage(player, "Information about preset §b" + preset.getName() + "§r" + C.NEW_LINE
                + "§bDescription:§r " + preset.getDescription() + C.NEW_LINE
                + "§bSchematic sets (" + schematicSetsList.size() + ")§r:" + C.NEW_LINE
                + String.join(C.NEW_LINE, schematicSetsList));
    }

    private void presetList(Player player) {
        String presetString = config.getPresets().stream()
                .map(preset -> "§bID: §r" + preset.getName() + C.NEW_LINE + "  §bDesc:§r " + preset.getDescription())
                .collect(Collectors.joining("\n"));
        messageSender().sendMessage(player, presetString);
    }

    /**
     * Saves a list of schematic sets to a preset.
     *
     * @param player        player for error handling
     * @param presetName    name of preset
     * @param schematicSets schematic sets
     */
    private boolean savePreset(Player player, String presetName, List<String> schematicSets) {
        if (config.presetExists(presetName)) {
            messageSender().sendError(player, "Preset §b" + presetName + "§r does already exist.");
            return false;
        }
        config.addPreset(new Preset(presetName, schematicSets));
        config.save();
        return true;
    }

    private List<String> getSchematicSets(BrushSettings brush) {
        return brush.getSchematicSets().stream()
                .map(SchematicSet::getArguments)
                .collect(Collectors.toList());
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
            if (config.presetExists(last)) {
                return Collections.singletonList("This name is already in use!");
            }
            return Collections.singletonList("<name of preset>");
        }

        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)
                || "info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, 50, config);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, 50, config);
            }
        }

        if ("save".equalsIgnoreCase(cmd) || "s".equalsIgnoreCase(cmd)) {
            if (args.length == 2) {
                if (config.presetExists(last)) {
                    return Collections.singletonList("This name is already in use!");
                }
                return Collections.singletonList("<name of preset>");
            }
            return TabUtil.getSchematicSetSyntax(args, schematicCache, config);
        }

        if ("appendSet".equalsIgnoreCase(cmd) || "ab".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, 50, config);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, 50, config);
            }

            return TabUtil.getSchematicSetSyntax(args, schematicCache, config);
        }

        if ("removeSet".equalsIgnoreCase(cmd) || "rb".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, 50, config);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, 50, config);
            }
            if (args.length == 3) {

                return Collections.singletonList("<id of schematic set>");
            }
        }

        if ("list".equalsIgnoreCase(cmd) || "l".equalsIgnoreCase(cmd)) {
            return Collections.emptyList();
        }

        if ("descr".equalsIgnoreCase(cmd) || "d".equalsIgnoreCase(cmd)) {
            if (args.length == 2 && last.isEmpty()) {
                List<String> presets = TabUtil.getPresets(last, 50, config);
                presets.add("<name of preset>");
                return presets;
            }
            if (args.length == 2) {
                return TabUtil.getPresets(last, 50, config);
            }

            return Collections.singletonList("<description>");
        }

        if (args.length == 1) {
            return ArrayUtil.startingWithInArray(cmd, COMMANDS).collect(Collectors.toList());
        }
        return null;
    }
}
