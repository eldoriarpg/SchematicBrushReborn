package de.eldoria.schematicbrush.commands.settings;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ChatBlock extends AdvancedCommand implements IPlayerTabExecutor {
    private final IMessageBlockerService messageBlocker;

    public ChatBlock(Plugin plugin, IMessageBlockerService messageBlocker) {
        super(plugin, CommandMeta.builder("chatblock")
                .hidden()
                .addUnlocalizedArgument("state", true)
                .build());
        this.messageBlocker = messageBlocker;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.asBoolean(0)) {
            messageBlocker.blockPlayer(player);
        } else {
            messageBlocker.unblockPlayer(player);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return Collections.emptyList();
    }
}
