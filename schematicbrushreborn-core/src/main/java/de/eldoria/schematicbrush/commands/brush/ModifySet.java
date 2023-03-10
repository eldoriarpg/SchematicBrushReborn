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
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ModifySet extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematics;

    public ModifySet(Plugin plugin, Sessions sessions, BrushSettingsRegistry registry, SchematicRegistry schematics) {
        super(plugin, CommandMeta.builder("modifySet")
                .addUnlocalizedArgument("id", true)
                .addUnlocalizedArgument("type", true)
                .addUnlocalizedArgument("value", false)
                .hidden()
                .build());
        this.sessions = sessions;
        this.registry = registry;
        this.schematics = schematics;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var builder = sessions.getOrCreateSession(player);
        var set = builder.getSchematicSet(args.asInt(0));
        if (set.isEmpty()) {
            messageSender().send(MessageChannel.ACTION_BAR, MessageType.ERROR, player, "Invalid set");
            return;
        }

        if ("selector".equalsIgnoreCase(args.asString(1))) {
            var selector = registry.parseSelector(args.subArguments().subArguments());
            CommandAssertions.isFalse(selector.select(player, schematics).isEmpty(), "No schematics matching this selector.");
            set.get().selector(selector);
            set.get().refreshSchematics(player, schematics);
        } else if ("weight".equalsIgnoreCase(args.asString(1))) {
            if (args.asInt(2) != -1) {
                CommandAssertions.range(args.asInt(2), 1, 100);
            }
            set.get().withWeight(args.asInt(2));
        } else {
            var mutator = registry.parseSchematicModifier(args.subArguments());
            set.get().withMutator(mutator.first, mutator.second);
        }

        sessions.showSet(player, args.asInt(0));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        if (args.size() == 1) {
            return Collections.singletonList("<id>");
        }

        if (args.size() == 2) {
            var strings = registry.completeSchematicModifier(args.subArguments());
            strings.addAll(TabCompleteUtil.complete(args.asString(1), "selector", "weight"));
            return strings;
        }

        if ("selector".equalsIgnoreCase(args.asString(1))) {
            return registry.completeSelector(args.subArguments().subArguments(), player);
        }
        if ("weight".equalsIgnoreCase(args.asString(1))) {
            return TabCompleteUtil.completeInt(args.asString(2), -1, 100);
        }
        return registry.completeSchematicModifier(args.subArguments());
    }
}
