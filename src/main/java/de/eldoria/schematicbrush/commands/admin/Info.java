package de.eldoria.schematicbrush.commands.admin;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.schematicbrush.C;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Info extends AdvancedCommand implements ITabExecutor {
    private final SchematicBrushReborn instance;

    public Info(SchematicBrushReborn plugin) {
        super(plugin, CommandMeta.builder("info")
                .build());
        instance = plugin;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var descr = instance.getDescription();
        var info = "§bSchematic Brush Reborn§r by §b" + String.join(", ", descr.getAuthors()) + "§r" + C.NEW_LINE
                   + "§bVersion§r : " + descr.getVersion() + C.NEW_LINE
                   + "§bSpigot:§r " + descr.getWebsite() + C.NEW_LINE
                   + "§bSupport:§r https://discord.gg/zRW9Vpu";
        messageSender().sendMessage(sender, info);
    }
}
