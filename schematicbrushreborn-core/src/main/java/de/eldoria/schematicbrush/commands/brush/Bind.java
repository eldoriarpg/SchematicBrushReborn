package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Bind extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;

    public Bind(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("bind").build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);
        // TODO: check if brush is valid

        CommandAssertions.isFalse(session.getSchematicCount() == 0, "Brush is empty.");
        var brush = session.build(plugin(), player);

        WorldEditBrush.setBrush(player, brush);
        messageSender().sendMessage(player, "Brush bound.");
    }
}
