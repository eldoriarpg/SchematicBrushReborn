/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.settings;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.sections.MessageChannel;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ShowNames extends AdvancedCommand implements IPlayerTabExecutor {
    private final NotifyListener listener;
    private final Configuration configuration;

    public ShowNames(Plugin plugin, NotifyListener listener, Configuration configuration) {
        super(plugin, CommandMeta.builder("showNames")
                .addUnlocalizedArgument("state", true)
                .addUnlocalizedArgument("channel", false)
                .withPermission(Permissions.Brush.USE)
                .build());
        this.listener = listener;
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var messageChannel = configuration.general().defaultNameChannel();
        if (args.sizeIs(2)) {
            messageChannel = args.asEnum(1, MessageChannel.class);
        }
        listener.setState(player, args.asBoolean(0), messageChannel);
        if (args.asBoolean(0)) {
            messageSender().sendMessage(player, "Names will be pasted.");
        } else {
            messageSender().sendMessage(player, "Names will be hidden.");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.sizeIs(1)) {
            return TabCompleteUtil.completeBoolean(args.asString(0));
        }
        if (args.sizeIs(2)) {
            return TabCompleteUtil.complete(args.asString(1), "CHAT", "TITLE", "SUB_TITLE", "ACTION_BAR");
        }
        return Collections.emptyList();
    }
}
