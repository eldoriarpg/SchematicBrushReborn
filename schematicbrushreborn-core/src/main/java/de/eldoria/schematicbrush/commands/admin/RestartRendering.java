/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.admin;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RestartRendering extends AdvancedCommand implements ITabExecutor {
    private final SchematicBrushRebornImpl instance;

    public RestartRendering(SchematicBrushRebornImpl plugin) {
        super(plugin, CommandMeta.builder("restartRendering")
                .withPermission(Permissions.Admin.RESTART_RENDERING)
                .build());
        instance = plugin;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) {
        instance.renderService().restart();
        messageSender().sendMessage(sender, "Rendering restarted");
    }
}
