/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.settings.preview;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.rendering.RenderService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Enable extends AdvancedCommand implements IPlayerTabExecutor {
    private final RenderService renderService;

    public Enable(Plugin plugin, RenderService renderService) {
        super(plugin, CommandMeta.builder("enable")
                .addUnlocalizedArgument("state", false)
                .build());
        this.renderService = renderService;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {

        boolean state = args.isEmpty() ? !renderService.getState(player) : args.asBoolean(0);
        renderService.setState(player, state);
        messageSender().sendMessage(player, state ? "Preview active." : "Preview disabled.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return TabCompleteUtil.completeBoolean(args.asString(0));
    }
}
