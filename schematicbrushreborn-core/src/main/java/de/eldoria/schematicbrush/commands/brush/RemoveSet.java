/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class RemoveSet extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;

    public RemoveSet(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("removeset")
                .addUnlocalizedArgument("id", true)
                .hidden()
                .build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var success = sessions.getOrCreateSession(player).removeSchematicSet(args.asInt(0));
        if (!success) {
            messageSender().send(MessageChannel.ACTION_BAR, MessageType.ERROR, player, "Invalid set.");
        }
        sessions.showSets(player);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.size() == 1) {
            var size = sessions.getOrCreateSession(player).schematicSets().size();
            return TabCompleteUtil.completeInt(args.asString(0), 0, size - 1);
        }
        return Collections.emptyList();
    }
}
