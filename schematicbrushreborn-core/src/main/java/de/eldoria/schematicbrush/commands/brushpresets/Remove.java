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
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Remove extends AdvancedCommand implements IPlayerTabExecutor {
    private final Storage storage;

    public Remove(Plugin plugin, Storage storage) {
        super(plugin, CommandMeta.builder("remove")
                .addUnlocalizedArgument("name", true)
                .hidden()
                .build());
        this.storage = storage;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        CompletableFuture<Boolean> removal;
        if (args.flags().has("g")) {
            CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);
            removal = storage.brushes().globalContainer().remove(name)
                    .whenComplete(Futures.whenComplete(
                            success -> CommandAssertions.isTrue(success, "error.unkownBrush", Replacement.create("name", name).addFormatting('b')),
                            err -> handleCommandError(player, err)));
        } else {
            removal = storage.brushes().playerContainer(player).remove(name)
                    .whenComplete(Futures.whenComplete(
                            success -> CommandAssertions.isTrue(success, "error.unkownBrush", Replacement.create("name", name).addFormatting('b')),
                            err -> handleCommandError(player, err)));
        }
        removal.whenComplete(Futures.whenComplete(
                succ -> messageSender().sendMessage(player, "Brush §b" + name + "§r deleted!"),
                err -> handleCommandError(player, err)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return storage.brushes().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
