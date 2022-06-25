/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.preset.Preset;
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

    private final Storage config;
    private final Sessions sessions;

    public SaveBrush(Plugin plugin, Sessions sessions, Storage storage) {
        super(plugin, CommandMeta.builder("saveBrush")
                .addUnlocalizedArgument("name", true)
                .withPermission(Permissions.BrushPreset.USE)
                .hidden()
                .build());
        this.config = storage;
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);

        var schematicSets = session.snapshot();
        var brush = new Brush(args.asString(0), schematicSets);
        CompletableFuture<Optional<Brush>> addition;
        if (args.flags().has("g")) {
            CommandAssertions.permission(player, false, Permissions.BrushPreset.GLOBAL);
            addition = config.brushes().globalContainer().get(brush.name())
                    .whenComplete(Futures.whenComplete(succ -> {
                        if (succ.isPresent()) {
                            CommandAssertions.isTrue(args.flags().has("f"), "Brush already exists. Use -f to override");
                        }
                        config.brushes().globalContainer().add(brush).join();
                    }, err -> handleCommandError(player, err)));
        } else {
            addition = config.brushes().playerContainer(player).get(brush.name())
                    .whenComplete(Futures.whenComplete(succ -> {
                        if (succ.isPresent()) {
                            CommandAssertions.isTrue(args.flags().has("f"), "Brush already exists. Use -f to override");
                        }
                        config.brushes().playerContainer(player).add(brush).join();
                    }, err -> handleCommandError(player, err)));
        }
        addition.whenComplete(Futures.whenComplete(res -> {
            //TODO: Think about storage saving
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
