/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Redo extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;

    public Redo(Plugin plugin, Sessions sessions) {
        super(plugin, CommandMeta.builder("redo")
                .hidden()
                .build());
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (!sessions.redo(player)) {
            messageSender().sendErrorActionBar(player, "commands.brush.redo.empty");
        }
        sessions.showBrush(player);
    }
}
