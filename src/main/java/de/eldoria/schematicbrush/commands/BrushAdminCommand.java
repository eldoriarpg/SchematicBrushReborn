package de.eldoria.schematicbrush.commands;

import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.commands.util.MessageSender;
import de.eldoria.schematicbrush.commands.util.TabUtil;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BrushAdminCommand implements TabExecutor {
    private final Plugin plugin;
    private final SchematicCache cache;
    private static final String[] COMMANDS = {"info", "reload", "reloadschematics"};

    public BrushAdminCommand(Plugin plugin, SchematicCache cache) {
        this.plugin = plugin;
        this.cache = cache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            info(sender);
            return true;
        }
        String arg = args[0];

        if ("info".equalsIgnoreCase(arg) || "i".equalsIgnoreCase(arg)) {
            info(sender);
            return true;
        }

        if ("reload".equalsIgnoreCase(arg)) {
            if (sender.hasPermission("schematicbrush.reload")) {
                reload(sender);
            } else {
                if (sender instanceof Player) {
                    MessageSender.sendMessage((Player) sender, "You don't have the permission to do this!");
                }
            }
            return true;
        }
        if ("reloadschematics".equalsIgnoreCase(arg)) {
            if (sender.hasPermission("schematicbrush.reloadschematics")) {
                reloadSchematics(sender);
            } else {
                if (sender instanceof Player) {
                    MessageSender.sendMessage((Player) sender, "You don't have the permission to do this!");
                }
            }
            return true;
        }
        return true;
    }

    private void help(CommandSender sender) {

    }

    private void info(CommandSender sender) {
        PluginDescriptionFile descr = plugin.getDescription();
        String info = "Schematic Brush Reborn by " + String.join(", ", descr.getAuthors())
                + "Version : " + descr.getVersion() + C.NEW_LINE
                + "Spigot: " + descr.getWebsite();
        if (sender instanceof ConsoleCommandSender) {
            plugin.getLogger().info(info);
        } else if (sender instanceof Player) {
            MessageSender.sendMessage((Player) sender, info);
        }

    }

    private void reload(CommandSender sender) {
        cache.reload();
        if (sender instanceof ConsoleCommandSender) {
            plugin.getLogger().info("Schematic Brush Reborn reloaded.");
        } else if (sender instanceof Player) {
            MessageSender.sendMessage((Player) sender, "Schematic Brush Reborn reloaded.");
        }
    }

    private void reloadSchematics(CommandSender sender) {
        cache.reload();
        if (sender instanceof ConsoleCommandSender) {
            plugin.getLogger().info("Schematics reloaded.");
        } else if (sender instanceof Player) {
            MessageSender.sendMessage((Player) sender, "Schematics reloaded");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return List.of(COMMANDS);
        }

        if (args.length == 1) {
            return TabUtil.startingWithInArray(args[0], COMMANDS).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
