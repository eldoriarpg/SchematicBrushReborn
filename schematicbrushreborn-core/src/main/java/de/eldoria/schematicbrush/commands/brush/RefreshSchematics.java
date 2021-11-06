/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
        sessions.showBrush(player);
        messageSender().sendMessage(player, "Schematics refreshed.");
    }
}
