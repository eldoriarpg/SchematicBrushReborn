/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.commands.settings.ChatBlock;
import de.eldoria.schematicbrush.commands.settings.Preview;
import de.eldoria.schematicbrush.commands.settings.ShowNames;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.listener.NotifyListener;
import de.eldoria.schematicbrush.rendering.RenderService;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.plugin.Plugin;

public class Settings extends AdvancedCommand {
    public Settings(Plugin plugin, Configuration configuration, RenderService renderService, NotifyListener notifyListener, MessageBlocker messageBlocker) {
        super(plugin);
        meta(CommandMeta.builder("sbrs")
                .withPermission(Permissions.Brush.USE)
                .withSubCommand(new Preview(plugin, renderService))
                .withSubCommand(new ShowNames(plugin, notifyListener, configuration))
                .withSubCommand(new ChatBlock(plugin, messageBlocker))
                .build());
    }
}
