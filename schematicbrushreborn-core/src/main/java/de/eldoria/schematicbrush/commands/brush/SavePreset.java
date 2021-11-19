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
import de.eldoria.schematicbrush.config.ConfigurationImpl;
import de.eldoria.schematicbrush.config.sections.presets.PresetImpl;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SavePreset extends AdvancedCommand implements IPlayerTabExecutor {

    private final ConfigurationImpl config;
    private final Sessions sessions;

    public SavePreset(Plugin plugin, Sessions sessions, ConfigurationImpl config) {
        super(plugin, CommandMeta.builder("savePreset")
                .addUnlocalizedArgument("name", true)
                .withPermission(Permissions.Preset.USE)
                .hidden()
                .build());
        this.config = config;
        this.sessions = sessions;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);

        var schematicSets = session.schematicSets();
        CommandAssertions.isFalse(schematicSets.isEmpty(), "Brush is empty.");
        var preset = new PresetImpl(args.asString(0), schematicSets);
        if (args.flags().has("g")) {
            CommandAssertions.permission(player, false, Permissions.Preset.GLOBAL);
            config.presets().addPreset(preset);
        } else {
            config.presets().addPreset(player, preset);
        }
        config.save();
        messageSender().sendMessage(player, "Preset saved.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        if (args.size() == 1) {
            return Collections.singletonList("<name>");
        }
        return Collections.emptyList();
    }
}
