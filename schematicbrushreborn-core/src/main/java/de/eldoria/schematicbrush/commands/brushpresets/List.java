/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brushpresets;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.commands.brushpresets.info.Global;
import de.eldoria.schematicbrush.commands.brushpresets.info.Private;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import org.bukkit.plugin.Plugin;

public class List extends AdvancedCommand {

    public List(Plugin plugin, StorageRegistry storage, BrushSettingsRegistry registry) {
        super(plugin, CommandMeta.builder("list")
                .buildSubCommands((cmds, builder) -> {
                    var privateList = new Private(plugin, storage, registry);
                    var globalList = new Global(plugin, storage, registry);
                    builder.withDefaultCommand(privateList);
                    cmds.add(privateList);
                    cmds.add(globalList);
                })
                .build());
    }
}
