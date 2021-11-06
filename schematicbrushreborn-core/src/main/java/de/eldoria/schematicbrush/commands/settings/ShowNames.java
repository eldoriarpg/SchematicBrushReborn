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
