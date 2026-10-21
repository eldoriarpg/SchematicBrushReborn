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
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SavePreset extends AdvancedCommand implements IPlayerTabExecutor {

    private final StorageRegistry storage;
    private final Sessions sessions;

    public SavePreset(Plugin plugin, Sessions sessions, StorageRegistry storage) {
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
        CommandAssertions.isFalse(schematicSets.isEmpty(), "error.emptyBrush");
        var preset = new Preset(args.asString(0), schematicSets);
        var global = args.flags().has("g");
        if (global) CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);
        var force = args.flags().has("f");
        var holder = storage.activeStorage().presets();
        var container = global ? holder.globalContainer() : holder.playerContainer(player);
        container.get(preset.name())
                 .thenCompose(existing -> {
                     CommandAssertions.isFalse(existing.isPresent() && !force, "error.presetExists");
                     return container.add(preset);
                 })
                 .thenRun(() -> storage.activeStorage().save())
                 .whenComplete(Futures.whenComplete(
                         done -> messageSender().sendMessage(player, "commands.brush.savePreset.saved"),
                         err -> handleCommandError(player, err)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return Collections.singletonList("<name>");
        }
        return Collections.emptyList();
    }
}
