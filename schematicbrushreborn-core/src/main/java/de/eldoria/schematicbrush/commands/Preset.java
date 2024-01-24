/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.commands.preset.Descr;
import de.eldoria.schematicbrush.commands.preset.Info;
import de.eldoria.schematicbrush.commands.preset.List;
import de.eldoria.schematicbrush.commands.preset.Remove;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;


/**
 * Brush to create and modify brush presets.
 */
public class Preset extends AdvancedCommand {
    public Preset(Plugin plugin, StorageRegistry storage, MessageBlocker messageBlocker) {
        super(plugin);
        meta(CommandMeta.builder("sbrp")
                .withPermission(Permissions.Preset.USE)
                .buildSubCommands((cmds, builder) -> {
                    var list = new List(plugin, storage, messageBlocker);
                    builder.withDefaultCommand(list);
                    cmds.add(new Descr(plugin, storage));
                    cmds.add(new Info(plugin, storage, messageBlocker));
                    cmds.add(list);
                    cmds.add(new Remove(plugin, storage));
                }).build());
    }
}
