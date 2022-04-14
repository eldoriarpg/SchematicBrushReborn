/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
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
    public Preset(Plugin plugin, ConfigurationImpl config, MessageBlocker messageBlocker) {
        super(plugin);
        meta(CommandMeta.builder("sbrp")
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
