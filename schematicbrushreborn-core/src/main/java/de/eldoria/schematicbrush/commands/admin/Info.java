/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 */

package de.eldoria.schematicbrush.commands.admin;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.executor.ITabExecutor;
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Info extends AdvancedCommand implements ITabExecutor {
    private final SchematicBrushRebornImpl instance;

    public Info(SchematicBrushRebornImpl plugin) {
        super(plugin, CommandMeta.builder("info")
                .build());
        instance = plugin;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) {
        var descr = instance.getDescription();
        var info = "§bSchematic Brush Reborn§r by §b" + String.join(", ", descr.getAuthors()) + "§r\n"
                   + "§bVersion§r : " + descr.getVersion() + "\n"
                   + "§bSpigot:§r " + descr.getWebsite() + "\n"
                   + "§bSupport:§r https://discord.gg/zRW9Vpu";
        messageSender().sendMessage(sender, info);
    }
}
