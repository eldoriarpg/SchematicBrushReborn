/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SaveBrush extends AdvancedCommand implements IPlayerTabExecutor {

    private final Storage storage;
    private final Sessions sessions;

    public SaveBrush(Plugin plugin, Sessions sessions, Storage storage) {
        super(plugin, CommandMeta.builder("saveBrush")
                .addUnlocalizedArgument("name", true)
                .withPermission(Permissions.BrushPreset.USE)
                .hidden()
                .build());
        this.storage = storage;
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);

        var brush = new Brush(args.asString(0), session);
        CompletableFuture<Optional<Brush>> addition;
        if (args.flags().has("g")) {
            CommandAssertions.permission(player, false, Permissions.BrushPreset.GLOBAL);
            addition = storage.brushes().globalContainer().get(brush.name())
                    .whenComplete(Futures.whenComplete(succ -> {
                        if (succ.isPresent()) {
                            CommandAssertions.isTrue(args.flags().has("f"), "Brush already exists. Use -f to override");
                        }
                        storage.brushes().globalContainer().add(brush).join();
                    }, err -> handleCommandError(player, err)));
        } else {
            addition = storage.brushes().playerContainer(player).get(brush.name())
                    .whenComplete(Futures.whenComplete(succ -> {
                        if (succ.isPresent()) {
                            CommandAssertions.isTrue(args.flags().has("f"), "Brush already exists. Use -f to override");
                        }
                        storage.brushes().playerContainer(player).add(brush).join();
                    }, err -> handleCommandError(player, err)));
        }
        addition.whenComplete(Futures.whenComplete(res -> {
            storage.save();
            messageSender().sendMessage(player, "Brush saved.");
        }, err -> handleCommandError(player, err)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return Collections.singletonList("<name>");
        }
        return Collections.emptyList();
    }
}
