/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.modify;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.schematics.SchematicSelection;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Selection extends AdvancedCommand implements IPlayerTabExecutor {
    private final BrushSettingsRegistry registry;

    public Selection(BrushSettingsRegistry registry, Plugin plugin) {
        super(plugin, CommandMeta.builder("selection")
                .addUnlocalizedArgument("mode", true)
                .build());
        this.registry = registry;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        Optional<SchematicBrush> schematicBrush = WorldEditBrush.getSchematicBrush(player);
        CommandAssertions.isTrue(schematicBrush.isPresent(), "You are not holding a schematic brush");
        SchematicSelection schematicSelection = registry.parseSchematicSelection(args);
        schematicBrush.get().settings().schematicSelection(schematicSelection);
        messageSender().send(MessageChannel.CHAT, MessageType.NORMAL, player, "Schematic selection changed.");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        return registry.completeSchematicSelection(args, player);
    }
}
