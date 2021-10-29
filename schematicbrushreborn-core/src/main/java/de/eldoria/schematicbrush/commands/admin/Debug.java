package de.eldoria.schematicbrush.commands.admin;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.eldoutilities.debug.DebugSettings;
import de.eldoria.eldoutilities.debug.DebugUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Debug extends AdvancedCommand implements ITabExecutor {
    public Debug(Plugin plugin) {
        super(plugin, CommandMeta.builder("debug")
                .withPermission("schematicbrush.admin.debug")
                .build());
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        DebugUtil.dispatchDebug(sender, plugin(), DebugSettings.DEFAULT);
    }
}
