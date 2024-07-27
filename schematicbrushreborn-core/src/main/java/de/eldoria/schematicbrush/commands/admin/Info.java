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
import de.eldoria.eldoutilities.debug.UserData;
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Info extends AdvancedCommand implements ITabExecutor {
    private final SchematicBrushRebornImpl instance;
    private final UserData userData;

    public Info(SchematicBrushRebornImpl plugin) {
        super(plugin, CommandMeta.builder("info")
                .build());
        instance = plugin;
        userData = UserData.get(plugin());
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) {
        var descr = instance.getDescription();
        var info = "<heading>Schematic Brush Reborn§r by <value>" + String.join(", ", descr.getAuthors()) + "<default>\n"
                   + "<name>Premium:<value> " + userData.isPremium() + "\n"
                   + "<name>Version:<value> " + descr.getVersion() + "\n"
                   + "<name>Spigot:<value> https://www.spigotmc.org/resources/98499\n"
                   + "<name>Patreon:<value> https://www.patreon.com/eldoriaplugins\n"
                   + "<name>Support:<value> https://discord.gg/zRW9Vpu";
        messageSender().sendMessage(sender, info);
    }
}
