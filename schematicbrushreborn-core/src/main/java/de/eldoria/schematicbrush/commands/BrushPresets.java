/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.commands.brushpresets.Descr;
import de.eldoria.schematicbrush.commands.brushpresets.Info;
import de.eldoria.schematicbrush.commands.brushpresets.List;
import de.eldoria.schematicbrush.commands.brushpresets.Remove;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;


/**
 * Brush to create and modify brush presets.
 */
public class BrushPresets extends AdvancedCommand {
    public BrushPresets(Plugin plugin, Storage config, MessageBlocker messageBlocker, BrushSettingsRegistry registry) {
        super(plugin);
        meta(CommandMeta.builder("sbrbp")
                .withPermission(Permissions.BrushPreset.USE)
                .buildSubCommands((cmds, builder) -> {
                    var list = new List(plugin, config, messageBlocker, registry);
                    builder.withDefaultCommand(list);
                    cmds.add(new Descr(plugin, config));
                    cmds.add(new Info(plugin, config, messageBlocker, registry));
                    cmds.add(list);
                    cmds.add(new Remove(plugin, config));
                }).build());
    }
}
