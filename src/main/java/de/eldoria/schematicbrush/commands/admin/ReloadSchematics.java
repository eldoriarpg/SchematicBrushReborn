package de.eldoria.schematicbrush.commands.admin;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadSchematics extends AdvancedCommand implements ITabExecutor {
    private final SchematicCache cache;

    public ReloadSchematics(SchematicBrushReborn plugin, SchematicCache cache) {
        super(plugin, CommandMeta.builder("reloadSchematics")
                .withPermission("schematicbrush.admin.reloadschematics")
                .build());
        this.cache = cache;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        cache.reload();
        messageSender().sendMessage(sender, "Schematics reloaded");
    }
}
