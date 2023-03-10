/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brushpresets;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.commands.brushpresets.info.Global;
import de.eldoria.schematicbrush.commands.brushpresets.info.Private;
import de.eldoria.schematicbrush.storage.Storage;
import org.bukkit.plugin.Plugin;

public class List extends AdvancedCommand {

    public List(Plugin plugin, Storage storage, MessageBlocker messageBlocker, BrushSettingsRegistry registry) {
        super(plugin, CommandMeta.builder("list")
                .buildSubCommands((cmds, builder) ->{
                    var privateList = new Private(plugin, storage, messageBlocker, registry);
                    var globalList = new Global(plugin, storage, messageBlocker, registry);
                    builder.withDefaultCommand(privateList);
                    cmds.add(privateList);
                    cmds.add(globalList);
                })
                .build());
    }
}
