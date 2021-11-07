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
import de.eldoria.schematicbrush.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AddPreset extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final Configuration configuration;

    public AddPreset(Plugin plugin, Sessions sessions, Configuration configuration) {
        super(plugin, CommandMeta.builder("addpreset")
                .addUnlocalizedArgument("name", true)
                .build());
        this.sessions = sessions;
        this.configuration = configuration;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);

        var preset = configuration.presets().getPreset(player, args.asString(0));
        CommandAssertions.isTrue(preset.isPresent(), "Unkown preset.");

        for (var builder : preset.get().schematicSets()) {
            session.addSchematicSet(builder);
        }
        sessions.showBrush(player);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return configuration.presets().complete(player, args.asString(0));
        }
        return Collections.emptyList();
    }
}
