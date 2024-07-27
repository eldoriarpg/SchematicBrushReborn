/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.settings;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.schematicbrush.commands.settings.preview.Enable;
import de.eldoria.schematicbrush.commands.settings.preview.Subscribe;
import de.eldoria.schematicbrush.commands.settings.preview.Unsubscribe;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;

public class Preview extends AdvancedCommand {
    public Preview(Plugin plugin, RenderService renderService) {
        super(plugin, CommandMeta.builder("preview")
                .withPermission(Permissions.Brush.PREVIEW)
                .withSubCommand(new Enable(plugin, renderService))
                .withSubCommand(new Subscribe(plugin, renderService))
                .withSubCommand(new Unsubscribe(plugin, renderService))
                .build());
    }
}
