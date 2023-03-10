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
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class RefreshSchematics extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BrushSettingsRegistry settingsRegistry;
    private final SchematicRegistry schematicRegistry;

    public RefreshSchematics(Plugin plugin, Sessions sessions, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        super(plugin, CommandMeta.builder("refreshSchematics")
                .hidden()
                .build());
        this.sessions = sessions;
        this.settingsRegistry = settingsRegistry;
        this.schematicRegistry = schematicRegistry;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = false;
        if (!args.isEmpty()) {
            session = "session".equalsIgnoreCase(args.asString(0));
        }
        if (session) {
            refreshSession(player);
        } else {
            refreshBrush(player);
        }
    }

    private void refreshBrush(@NotNull Player player) throws CommandException {
        if (!(WorldEditBrush.getBrush(player) instanceof SchematicBrush brush)) {
            throw CommandException.message("error.notABrush");
        }
        var brushBuilder = brush.toBuilder(settingsRegistry, schematicRegistry);
        brushBuilder.refresh();
        if (WorldEditBrush.setBrush(player, brushBuilder.build(plugin(), player))) {
            messageSender().sendMessage(player, "Brush schematics refreshed.");
        } else {
            messageSender().sendError(player, "Could not refresh schematics.");
        }
    }

    private void refreshSession(@NotNull Player player) {
        var builder = sessions.getOrCreateSession(player);
        builder.refresh();
        sessions.showSets(player);
        messageSender().sendMessage(player, "Schematics refreshed.");
    }
}
