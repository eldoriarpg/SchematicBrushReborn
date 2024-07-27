/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.settings.preview;

import de.eldoria.eldoutilities.commands.Completion;
import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.schematicbrush.rendering.RenderService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Subscribe extends AdvancedCommand implements IPlayerTabExecutor {
    private final RenderService renderService;

    public Subscribe(Plugin plugin, RenderService renderService) {
        super(plugin, CommandMeta.builder("subscribe")
                .addUnlocalizedArgument("player", true)
                .build());
        this.renderService = renderService;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var target = args.asPlayer(0);
        if (renderService.subscribe(target, player)) {
            messageSender().sendMessage(player, "commands.settings.preview.subscribe.subscribed");
        } else {
            messageSender().sendMessage(player, "commands.settings.preview.subscribe.notEnabled");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return Completion.completeOnlinePlayers(args.asString(0));
    }
}
