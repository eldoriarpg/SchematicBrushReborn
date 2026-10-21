/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brushpresets;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.messages.Replacement;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Remove extends AdvancedCommand implements IPlayerTabExecutor {
    private final StorageRegistry storage;

    public Remove(Plugin plugin, StorageRegistry storage) {
        super(plugin, CommandMeta.builder("remove")
                .addUnlocalizedArgument("name", true)
                .hidden()
                .build());
        this.storage = storage;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        var global = args.flags().has("g");
        if (global) CommandAssertions.permission(player, false, Permissions.BrushPreset.GLOBAL);
        var holder = storage.activeStorage().brushes();
        var container = global ? holder.globalContainer() : holder.playerContainer(player);
        container.remove(name)
                 .thenAccept(removed -> {
                     CommandAssertions.isTrue(removed, "error.unknownBrush", Replacement.unparsed("name", name));
                     storage.activeStorage().save();
                 })
                 .whenComplete(Futures.whenComplete(
                         done -> messageSender().sendMessage(player, "commands.brushPresets.remove.removed", Replacement.unparsed("name", name)),
                         err -> handleCommandError(player, err)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return storage.activeStorage().brushes().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
