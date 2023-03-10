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
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AddSetModifier extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BrushSettingsRegistry registry;

    public AddSetModifier(Plugin plugin, Sessions sessions, BrushSettingsRegistry registry) {
        super(plugin, CommandMeta.builder("addsetmodifier")
                .addUnlocalizedArgument("id", true)
                .addUnlocalizedArgument("modifier", true)
                .hidden()
                .build());
        this.sessions = sessions;
        this.registry = registry;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var session = sessions.getOrCreateSession(player);

        var modifier = args.get(1).asString();

        var registration = registry.getSchematicModifier(modifier);
        CommandAssertions.isTrue(registration.isPresent(), "Unknown modifier.");
        var setId = args.get(0).asInt();
        var schematicSet = session.getSchematicSet(setId);

        CommandAssertions.isTrue(schematicSet.isPresent(), "Invalid set id");

        schematicSet.get().setModifier(registration.get().modifier(), registration.get().mutators().get(0).defaultSetting());
        sessions.showSet(player, setId);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        return Collections.emptyList();
    }
}
