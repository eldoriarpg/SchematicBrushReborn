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
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCache extends AdvancedCommand implements ITabExecutor {
    private final SchematicRegistry cache;

    public ReloadCache(SchematicBrushRebornImpl plugin, SchematicRegistry cache) {
        super(plugin, CommandMeta.builder("reloadCache")
                .withPermission(Permissions.Admin.RELOAD_CACHE)
                .build());
        this.cache = cache;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Arguments args) {
        cache.reload();
        messageSender().sendMessage(sender, "Schematics reloaded");
    }
}
