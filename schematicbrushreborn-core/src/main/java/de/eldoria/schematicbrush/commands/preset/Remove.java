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

package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Remove extends AdvancedCommand implements IPlayerTabExecutor {
    private final Configuration configuration;

    public Remove(Plugin plugin, Configuration configuration) {
        super(plugin, CommandMeta.builder("remove")
                .addUnlocalizedArgument("name", true)
                .build());
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        if (args.hasFlag("g")) {
            CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);
            CommandAssertions.isTrue(configuration.presets().removePreset(name), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));
        } else {
            CommandAssertions.isTrue(configuration.presets().removePreset(player, name), "error.unkownPreset", Replacement.create("name", name).addFormatting('b'));
        }

        messageSender().sendMessage(player, "Preset §b" + name + "§r deleted!");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return configuration.presets().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
