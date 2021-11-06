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
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import de.eldoria.schematicbrush.commands.admin.Debug;
import de.eldoria.schematicbrush.commands.admin.Info;
import de.eldoria.schematicbrush.commands.admin.Reload;
import de.eldoria.schematicbrush.commands.admin.ReloadCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;

public class Admin extends AdvancedCommand {
    public Admin(SchematicBrushRebornImpl instance,
                 SchematicRegistry cache) {
        super(instance);
        meta(CommandMeta.builder("sba")
                .buildSubCommands((cmds, builder) -> {
                    var info = new Info(instance);
                    builder.withDefaultCommand(info);
                    cmds.add(info);
                    cmds.add(new Debug(instance));
                    cmds.add(new Reload(instance));
                    cmds.add(new ReloadCache(instance, cache));
                })
                .build());
    }
}
