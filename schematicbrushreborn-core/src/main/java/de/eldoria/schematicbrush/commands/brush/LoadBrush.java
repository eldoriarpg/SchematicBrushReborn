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
import de.eldoria.eldoutilities.utils.Consumers;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.storage.Storage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class LoadBrush extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final Storage storage;
    private final BrushSettingsRegistry settingsRegistry;
    private final SchematicRegistry schematicRegistry;

    public LoadBrush(Plugin plugin, Sessions sessions, Storage storage, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        super(plugin, CommandMeta.builder("loadbrush")
                .addUnlocalizedArgument("name", true)
                .build());
        this.sessions = sessions;
        this.storage = storage;
        this.settingsRegistry = settingsRegistry;
        this.schematicRegistry = schematicRegistry;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var name = args.asString(0);
        var strippedName = name.replaceAll("^g:", "");
        storage.brushes().containerByName(player, name)
                .get(strippedName)
                .whenComplete(Futures.whenComplete(brush -> {
                    CommandAssertions.isTrue(brush.isPresent(), "Unkown brush.");

                    sessions.setSession(player, brush.get().snapshot().load(player, settingsRegistry, schematicRegistry));
                    sessions.showBrush(player);
                }, err -> handleCommandError(player, err)))
                .whenComplete(Futures.whenComplete(Consumers.emptyConsumer(), err -> handleCommandError(player, err)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return storage.brushes().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
