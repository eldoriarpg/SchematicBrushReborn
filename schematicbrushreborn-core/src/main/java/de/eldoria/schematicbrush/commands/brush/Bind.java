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
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Bind extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final IMessageBlockerService messageBlocker;

    public Bind(Plugin plugin, Sessions sessions, IMessageBlockerService messageBlocker) {
        super(plugin, CommandMeta.builder("bind").build());
        this.sessions = sessions;
        this.messageBlocker = messageBlocker;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);

        CommandAssertions.isFalse(session.getSchematicCount() == 0, "Brush is empty.");
        var brush = session.build(plugin(), player);

        if (!WorldEditBrush.setBrush(player, brush)) {
            return;
        }

        var schematicCount = brush.getSettings().getSchematicCount();
        var setcount = brush.getSettings().schematicSets().size();
        messageBlocker.unblockPlayer(player).thenRun(() -> messageSender().sendMessage(player, String.format("Brush bound. Using §3%s§r Schematics in §3%s§r Sets", schematicCount, setcount)));
    }
}
