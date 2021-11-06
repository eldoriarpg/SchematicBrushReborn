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
import de.eldoria.schematicbrush.commands.settings.ChatBlock;
import de.eldoria.schematicbrush.commands.settings.Preview;
import de.eldoria.schematicbrush.commands.settings.ShowNames;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;

public class Settings extends AdvancedCommand {
    public Settings(Plugin plugin, RenderService renderService, NotifyListener notifyListener, IMessageBlockerService messageBlocker) {
        super(plugin);
        meta(CommandMeta.builder("sbs")
                .withPermission(Permissions.Brush.USE)
                .withSubCommand(new Preview(plugin, renderService))
                .withSubCommand(new ShowNames(plugin, notifyListener))
                .withSubCommand(new ChatBlock(plugin, messageBlocker))
                .build());
    }
}
