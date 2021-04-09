package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.debug.DebugSettings;
import de.eldoria.eldoutilities.debug.DebugUtil;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BrushAdminCommand extends EldoCommand {
    private static final String[] COMMANDS = {"info", "reload", "reloadschematics", "debug"};
    private final SchematicBrushReborn instance;
    private final SchematicCache cache;

    public BrushAdminCommand(SchematicBrushReborn instance,
                             SchematicCache cache) {
        super(instance);
        this.instance = instance;
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
            if (sender.hasPermission("schematicbrush.admin.reload")) {
                reload(sender);
            } else {
                if (sender instanceof Player) {
                    messageSender().sendMessage(sender, "You don't have the permission to do this!");
                }
            }
            return true;
        }


        if ("reloadschematics".equalsIgnoreCase(arg)) {
            if (sender.hasPermission("schematicbrush.admin.reloadschematics")) {
                reloadSchematics(sender);
            } else {
                if (sender instanceof Player) {
                    messageSender().sendMessage(sender, "You don't have the permission to do this!");
                }
            }
            return true;
        }

        if ("debug".equalsIgnoreCase(arg)) {
            if (sender.hasPermission("schematicbrush.admin.debug")) {
                DebugUtil.dispatchDebug(sender, getPlugin(), DebugSettings.DEFAULT);
            } else {
                messageSender().sendMessage(sender, "You don't have the permission to do this!");
            }
            return true;
        }

        if (sender instanceof Player) {
            messageSender().sendError(sender, "Invalid command!");
            return true;
        }

        sender.sendMessage("Invalid command");
        return true;
    }

    private void info(CommandSender sender) {
        PluginDescriptionFile descr = instance.getDescription();
        String info = "§bSchematic Brush Reborn§r by §b" + String.join(", ", descr.getAuthors()) + "§r" + C.NEW_LINE
                + "§bVersion§r : " + descr.getVersion() + C.NEW_LINE
                + "§bSpigot:§r " + descr.getWebsite() + C.NEW_LINE
                + "§bSupport:§r https://discord.gg/zRW9Vpu";
        if (sender instanceof ConsoleCommandSender) {
            instance.getLogger().info(info);
        } else if (sender instanceof Player) {
            messageSender().sendMessage(sender, info);
        }

    }

    private void reload(CommandSender sender) {
        instance.reload();
        if (sender instanceof ConsoleCommandSender) {
            instance.getLogger().info("Schematic Brush Reborn reloaded.");
        } else if (sender instanceof Player) {
            messageSender().sendMessage(sender, "Schematic Brush Reborn reloaded.");
        }
    }

    private void reloadSchematics(CommandSender sender) {
        cache.reload();
        if (sender instanceof ConsoleCommandSender) {
            instance.getLogger().info("Schematics reloaded.");
        } else if (sender instanceof Player) {
            messageSender().sendMessage(sender, "Schematics reloaded");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args[0].isEmpty()) {
            return Arrays.asList(COMMANDS);
        }

        if (args.length == 1) {
            return TabCompleteUtil.complete(args[0], COMMANDS);
        }

        return Collections.emptyList();
    }
}
