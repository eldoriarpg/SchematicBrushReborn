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

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.schematicbrush.commands.preset.Descr;
import de.eldoria.schematicbrush.commands.preset.Info;
import de.eldoria.schematicbrush.commands.preset.List;
import de.eldoria.schematicbrush.commands.preset.Remove;
import de.eldoria.schematicbrush.config.ConfigurationImpl;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;


/**
 * Brush to create and modify brush presets.
 */
public class Preset extends AdvancedCommand {
    public Preset(Plugin plugin, ConfigurationImpl config, IMessageBlockerService messageBlocker) {
        super(plugin);
        meta(CommandMeta.builder("sbp")
                .withPermission(Permissions.Preset.USE)
                .buildSubCommands((cmds, builder) -> {
                    var list = new List(plugin, config, messageBlocker);
                    builder.withDefaultCommand(list);
                    cmds.add(new Descr(plugin, config));
                    cmds.add(new Info(plugin, config, messageBlocker));
                    cmds.add(list);
                    cmds.add(new Remove(plugin, config));
                }).build());
    }
}
