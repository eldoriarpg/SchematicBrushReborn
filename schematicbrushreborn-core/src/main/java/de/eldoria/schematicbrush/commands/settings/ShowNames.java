/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.settings;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShowNames extends AdvancedCommand implements IPlayerTabExecutor {
    private final NotifyListener listener;

    public ShowNames(Plugin plugin, NotifyListener listener) {
        super(plugin, CommandMeta.builder("showNames")
                .addUnlocalizedArgument("state", true)
                .withPermission(Permissions.Brush.USE)
                .build());
        this.listener = listener;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        listener.setState(player, args.asBoolean(0));
        if (args.asBoolean(0)) {
            messageSender().sendMessage(player, "Names will be pasted.");
        } else {
            messageSender().sendMessage(player, "Names will be hidden.");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return TabCompleteUtil.completeBoolean(args.asString(0));
    }
}
