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
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.storage.Storage;
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

public class SavePreset extends AdvancedCommand implements IPlayerTabExecutor {

    private final Storage storage;
    private final Sessions sessions;

    public SavePreset(Plugin plugin, Sessions sessions, Storage storage) {
        super(plugin, CommandMeta.builder("savePreset")
                .addUnlocalizedArgument("name", true)
                .withPermission(Permissions.Preset.USE)
                .hidden()
                .build());
        this.storage = storage;
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);

        var schematicSets = session.schematicSets().stream().map(SchematicSetBuilder::copy).toList();
        CommandAssertions.isFalse(schematicSets.isEmpty(), "Brush is empty.");
        var preset = new Preset(args.asString(0), schematicSets);
        CompletableFuture<Optional<Preset>> addition;
        if (args.flags().has("g")) {
            CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);
            addition = storage.presets().globalContainer().get(preset.name())
                    .whenComplete(Futures.whenComplete(succ -> {
                        if (succ.isPresent()) {
                            CommandAssertions.isTrue(args.flags().has("f"), "Preset already exists. Use -f to override");
                        }
                        storage.presets().globalContainer().add(preset).join();
                    }, err -> handleCommandError(player, err)));
        } else {
            addition = storage.presets().playerContainer(player).get(preset.name())
                    .whenComplete(Futures.whenComplete(succ -> {
                        if (succ.isPresent()) {
                            CommandAssertions.isTrue(args.flags().has("f"), "Preset already exists. Use -f to override");
                        }
                        storage.presets().playerContainer(player).add(preset).join();
                    }, err -> handleCommandError(player, err)));
        }
        addition.whenComplete(Futures.whenComplete(res -> {
            //TODO: Think about storage saving
            storage.save();
            messageSender().sendMessage(player, "Preset saved.");
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
