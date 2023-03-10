/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.commands.modify.Next;
import de.eldoria.schematicbrush.commands.modify.Previous;
import de.eldoria.schematicbrush.commands.modify.Selection;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;


/**
 * Brush to create and modify brush presets.
 */
public class Modify extends AdvancedCommand {
    public Modify(Plugin plugin, BrushSettingsRegistry registry) {
        super(plugin);
        meta(CommandMeta.builder("sbrm")
                .withPermission(Permissions.Brush.USE)
                .buildSubCommands((cmds, builder) -> {
                    cmds.add(new Next(plugin));
                    cmds.add(new Previous(plugin));
                    cmds.add(new Selection(registry, plugin));
                }).build());
    }
}
