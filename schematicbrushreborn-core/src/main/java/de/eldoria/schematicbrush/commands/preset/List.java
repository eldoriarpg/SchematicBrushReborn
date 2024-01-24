/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.preset;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.commands.preset.info.Global;
import de.eldoria.schematicbrush.commands.preset.info.Private;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import org.bukkit.plugin.Plugin;

public class List extends AdvancedCommand {

    public List(Plugin plugin, StorageRegistry storage, MessageBlocker messageBlocker) {
        super(plugin, CommandMeta.builder("list")
                .buildSubCommands((cmds, builder) ->{
                    var privateList = new Private(plugin, storage, messageBlocker);
                    var globalList = new Global(plugin, storage, messageBlocker);
                    builder.withDefaultCommand(privateList);
                    cmds.add(privateList);
                    cmds.add(globalList);
                })
                .build());
    }
}
