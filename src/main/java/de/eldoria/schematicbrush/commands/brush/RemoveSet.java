package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RemoveSet extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;

    public RemoveSet(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("removeset")
                .addUnlocalizedArgument("id", true)
                .build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var success = sessions.getOrCreateSession(player).removeSchematicSet(args.asInt(0));
        if (!success) {
            messageSender().send(MessageChannel.ACTION_BAR, MessageType.ERROR, player, "Invalid set.");
        }
        sessions.showBrush(player);
    }
}
