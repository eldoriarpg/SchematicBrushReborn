package de.eldoria.schematicbrush.commands.admin;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload extends AdvancedCommand implements ITabExecutor {
    private final SchematicBrushReborn instance;

    public Reload(SchematicBrushReborn plugin) {
        super(plugin, CommandMeta.builder("reload")
                .withPermission("schematicbrush.admin.reload")
                .build());
        instance = plugin;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        instance.reload();
        messageSender().sendMessage(sender, "Schematic Brush Reborn reloaded.");
    }
}
