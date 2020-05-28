package de.eldoria.schematicbrush.commands;

import com.sk89q.worldedit.WorldEdit;
import de.eldoria.schematicbrush.MessageSender;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushConfiguration;
import de.eldoria.schematicbrush.brush.config.SubBrush;
import de.eldoria.schematicbrush.commands.parser.BrushSettingsParser;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;

/**
 * Brush to create and modify brush presets.
 */
public class BrushPresetCommand implements CommandExecutor {

    private final Plugin plugin;
    private final SchematicCache schematicCache;

    public BrushPresetCommand(Plugin plugin, SchematicCache schematicCache) {
        this.plugin = plugin;
        this.schematicCache = schematicCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Commands:
        // Save the current applied brush: /sbrpre savecurrent <id>
        // Save brush: /sbrpre current <id> <brushstrings>
        // Append a brush to preset: /sbrpre append <id> <brushstrings>
        // Append current brush to preset: /sbrpre append <id>
        // Remove a brush from preset: /sbrpre remove <id> <brushstrings|id>
        // Information about a preset: /sbrpre info <id>
        // List all presets with description: /sbrpre list
        // List all presets with description: /sbrpre descr <id> <description>

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can do this.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            help(player);
            return true;
        }

        String[] subcommandArgs = Arrays.copyOfRange(args, 1, args.length);

        String cmd = args[0];

        if ("current".equalsIgnoreCase(cmd) || "c".equalsIgnoreCase(cmd)) {
            save(player, subcommandArgs);
        }
        if ("save".equalsIgnoreCase(cmd) || "s".equalsIgnoreCase(cmd)) {
            savecurrent(player, subcommandArgs);
        }
        if ("appendbrush".equalsIgnoreCase(cmd) || "ab".equalsIgnoreCase(cmd)) {
            appendBrushes(player, subcommandArgs);
        }
        if ("removebrush".equalsIgnoreCase(cmd) || "rb".equalsIgnoreCase(cmd)) {
            removeBrushes(player, subcommandArgs);
        }
        if ("remove".equalsIgnoreCase(cmd) || "r".equalsIgnoreCase(cmd)) {
            removePreset(player, subcommandArgs);
        }
        if ("info".equalsIgnoreCase(cmd) || "i".equalsIgnoreCase(cmd)) {
            presetInfo(player, subcommandArgs);
        }
        if ("list".equalsIgnoreCase(cmd) || "l".equalsIgnoreCase(cmd)) {
            presetList(player);
        }
        if ("descr".equalsIgnoreCase(cmd) || "d".equalsIgnoreCase(cmd)) {
            setDescription(player, subcommandArgs);
        }


        return true;
    }

    private void help(Player player) {
        MessageSender.sendMessage(player,
                "This command allows you to save and modify brush presets." + lineSeparator()
                        + "/sbrp current <id> - Save your current equiped brush as a preset." + lineSeparator()
                        + "/sbrp save <id> <brushes...> - Save one or more brushes as a preset." + lineSeparator()
                        + "/sbrp appendbrush <id> <brushes...> - Add one or more brushes to a preset." + lineSeparator()
                        + "/sbrm descr - Set a description for a brush." + lineSeparator()
                        + "/sbrp removebrush <id> <id> - Remove brush from a preset." + lineSeparator()
                        + "/sbrp remove <id> - Remove a preset." + lineSeparator()
                        + "/sbrp info <id> - Get a list of brushes inside a preset." + lineSeparator()
                        + "/sbrm list - Get a list of all presets with description." + lineSeparator()
                        + "Use the id from the info command to change or remove a brush."
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

        String name = args[1];

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        Optional<SchematicBrush> schematicBrush = WorldEditBrushAdapter.getSchematicBrush(player);
        if (schematicBrush.isEmpty()) {
            MessageSender.sendError(player, "This tool is not a schematic brush");
        }

        SchematicBrush brush = schematicBrush.get();

        List<String> brushes = getBrushes(brush.getSettings());

        plugin.getConfig().contains("presets." + name + ".description");

        savePreset(player, name, brushes);
        setDescription(player, name, "none");
    }

    private void save(Player player, String[] args) {
        if (args.length != 2) {
            MessageSender.sendError(player, "Too few arguments");
        }
        String name = args[0];

        Object[] original;
        String[] brushArgs = Arrays.copyOfRange(args, 1, args.length);

        Optional<BrushConfiguration> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (settings.isEmpty()) {
            return;
        }

        List<String> brushes = getBrushes(settings.get());
        savePreset(player, name, brushes);
        setDescription(player, name, "none");
    }

    private void appendBrushes(Player player, String[] args) {
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments");
        }
        String name = args[0];

        Object[] original;
        String[] brushArgs = Arrays.copyOfRange(args, 1, args.length);

        Optional<BrushConfiguration> settings = BrushSettingsParser.parseBrush(player, plugin, schematicCache, args);

        if (settings.isEmpty()) {
            return;
        }

        addBrushes(player, name, getBrushes(settings.get()));
    }

    private void setDescription(Player player, String[] args) {
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments");
        }

        String name = args[0];

        setDescription(player, name, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
    }

    private void removeBrushes(Player player, String[] args) {
        if (args.length < 2) {
            MessageSender.sendError(player, "Too few arguments");
        }
        String name = args[0];

        Object[] original;
        String[] ids = Arrays.copyOfRange(args, 1, args.length);

        Optional<List<String>> optionalBrushes = getBrushesFromConfig(name);
        if (optionalBrushes.isEmpty()) {
            MessageSender.sendError(player, "Preset " + name + " does not exist.");
            return;
        }

        List<String> brushes = optionalBrushes.get();

        for (String id : ids) {
            try {
                int i = Integer.parseInt(id);
                if (i > brushes.size()) {
                    MessageSender.sendError(player, id + " is not a valid id.");
                    return;
                }
                brushes.set(i - 1, null);
            } catch (NumberFormatException e) {
                MessageSender.sendError(player, id + " is not a valid id.");
                return;
            }
        }

        overrideBrushes(player, name, brushes.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private void removePreset(Player player, String[] args) {
        if (args.length == 0) {
            MessageSender.sendError(player, "Too few Arguments");
            return;
        }

        String name = args[0];
        String path = "presets." + name;
        if (!plugin.getConfig().isSet(path)) {
            MessageSender.sendError(player, "Preset " + name + " does not exist.");
            return;
        }
        plugin.getConfig().set(path, null);
        plugin.saveConfig();
    }

    private void presetInfo(Player player, String[] args) {
        if (args.length == 0) {
            MessageSender.sendError(player, "Too few Arguments");
            return;
        }

        String name = args[0];
        String path = "presets." + name;

        Optional<List<String>> brushesFromConfig = getBrushesFromConfig(name);
        if (brushesFromConfig.isEmpty()) {
            MessageSender.sendError(player, "Preset " + name + " does not exist.");
            return;
        }
        List<String> brushString = brushesFromConfig.get();
        List<String> brushes = new ArrayList<>();
        for (int i = 0; i < brushString.size(); i++) {
            brushes.add((i + 1) + "| " + brushString.get(i));
        }

        String brushesList = brushes.stream().collect(Collectors.joining(lineSeparator()));
        MessageSender.sendMessage(player, "Information about preset " + name + lineSeparator()
                + getDescription(player, name) + lineSeparator() + "Brushes:" + lineSeparator() + brushesList);
    }


    private void presetList(Player player) {
        ConfigurationSection presets = plugin.getConfig().getConfigurationSection("presets");
        if (presets == null) {
            plugin.getLogger().warning("Preset section is missing!");
            return;
        }

        String presetString = presets.getKeys(false).stream()
                .map(k -> "ID: " + k + lineSeparator() + "  Desc: " + getDescription(player, k))
                .collect(Collectors.joining(lineSeparator()));
        MessageSender.sendMessage(player, presetString);
    }


    /**
     * Saves a list of brushes to a preset.
     *
     * @param player     player for error handling
     * @param presetName name of preset
     * @param brushArgs  arguments of brushes
     */
    private void savePreset(Player player, String presetName, List<String> brushArgs) {
        String path = "presets." + presetName + ".filter";
        boolean brushesPresent = plugin.getConfig().isSet(path);
        if (brushesPresent) {
            MessageSender.sendError(player, "Preset " + presetName + " does already exist.");
            return;
        }

        plugin.getConfig().set(path, brushArgs);
        plugin.saveConfig();
    }

    /**
     * Add a list of brushes to a existing preset.
     *
     * @param player     player for error handling
     * @param presetName name of preset
     * @param brushArgs  arguments of brushes
     */
    private void addBrushes(Player player, String presetName, List<String> brushArgs) {
        String path = "presets." + presetName + ".filter";
        boolean brushesPresent = plugin.getConfig().isSet(path);
        if (!brushesPresent) {
            MessageSender.sendError(player, "Preset " + presetName + " does not exist.");
            return;
        }

        brushArgs.addAll(plugin.getConfig().getStringList(path));

        plugin.getConfig().set(path, brushArgs);
        plugin.saveConfig();
    }

    /**
     * Replaces the current list of brushes of a preset with a new list.
     *
     * @param player     player for error handling
     * @param presetName name of preset
     * @param brushArgs  arguments of brushes
     */
    private void overrideBrushes(Player player, String presetName, List<String> brushArgs) {
        String path = "presets." + presetName + ".filter";
        boolean brushesPresent = plugin.getConfig().isSet(path);
        if (!brushesPresent) {
            MessageSender.sendError(player, "Preset " + presetName + "does not exist.");
            return;
        }
        brushArgs.addAll(plugin.getConfig().getStringList(path));

        plugin.getConfig().set(path, brushArgs);
        plugin.saveConfig();
    }

    private List<String> getBrushes(BrushConfiguration brush) {
        return brush.getBrushes().stream()
                .map(SubBrush::getArguments)
                .collect(Collectors.toList());
    }

    /**
     * Loads the brushes of a preset from config.
     *
     * @param presetName name of preset
     * @return optional list of brushes when the preset is present in config
     */
    private Optional<List<String>> getBrushesFromConfig(String presetName) {
        String path = "presets." + presetName + ".filter";
        if (plugin.getConfig().contains(path)) {
            return Optional.of(plugin.getConfig().getStringList(path));
        }
        return Optional.empty();
    }

    private void setDescription(Player player, String name, String descr) {
        String path = "presets." + name;
        boolean brushesPresent = plugin.getConfig().isSet(path);
        if (!brushesPresent) {
            MessageSender.sendError(player, "Preset " + name + "does not exist.");
            return;
        }
        plugin.getConfig().set(path + ".description", descr);
        plugin.saveConfig();
    }

    private String getDescription(Player player, String name) {
        String path = "presets." + name + ".description";
        boolean brushesPresent = plugin.getConfig().isSet(path);
        if (!brushesPresent) {
            MessageSender.sendError(player, "Preset " + name + "does not exist.");
            return null;
        }
        return plugin.getConfig().getString(path);
    }

}
