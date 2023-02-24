/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import de.eldoria.schematicbrush.commands.admin.Debug;
import de.eldoria.schematicbrush.commands.admin.Info;
import de.eldoria.schematicbrush.commands.admin.Migrate;
import de.eldoria.schematicbrush.commands.admin.Reload;
import de.eldoria.schematicbrush.commands.admin.ReloadCache;
import de.eldoria.schematicbrush.commands.admin.RestartRendering;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.storage.StorageRegistry;

public class Admin extends AdvancedCommand {
    public Admin(SchematicBrushRebornImpl instance,
                 SchematicRegistry cache, StorageRegistry storageRegistry) {
        super(instance);
        meta(CommandMeta.builder("sbra")
                .buildSubCommands((cmds, builder) -> {
                    var info = new Info(instance);
                    builder.withDefaultCommand(info);
                    cmds.add(info);
                    cmds.add(new Debug(instance));
                    cmds.add(new Reload(instance));
                    cmds.add(new ReloadCache(instance, cache));
                    cmds.add(new RestartRendering(instance));
                    cmds.add(new Migrate(instance, storageRegistry));
                })
                .build());
    }
}
