/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.command.AdvancedCommand;
import de.eldoria.eldoutilities.commands.command.CommandMeta;
import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.commands.executor.IPlayerTabExecutor;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrushMapper;
import de.eldoria.schematicbrush.brush.legacy.LegacyCompletion;
import de.eldoria.schematicbrush.brush.legacy.ParseException;
import de.eldoria.schematicbrush.brush.legacy.Parser;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.util.Escape;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Binds a brush written in the syntax of the original SchematicBrush, e.g.
 * {@code /sbr parse tree*@*:5 rock@90N^-1 -place:drop -incair}. The bound brush can be edited with {@code /sbr}.
 */
public class Parse extends AdvancedCommand implements IPlayerTabExecutor {
    private final Sessions sessions;
    private final Bind bind;
    private final StorageRegistry storage;
    private final LegacyBrushMapper mapper;
    private final LegacyCompletion completion;

    public Parse(Plugin plugin, Sessions sessions, Bind bind, StorageRegistry storage, LegacyBrushMapper mapper, LegacyCompletion completion) {
        super(plugin, CommandMeta.builder("parse")
                                 .addUnlocalizedArgument("expression", false)
                                 .build());
        this.sessions = sessions;
        this.bind = bind;
        this.storage = storage;
        this.mapper = mapper;
        this.completion = completion;
    }

    @Override
    public void onCommand(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) throws CommandException {
        var input = String.join(" ", RawArguments.of(args)).strip();
        LegacyBrush brush;
        try {
            brush = Parser.parse(input);
        } catch (ParseException e) {
            sendError(player, input, e);
            return;
        }
        var lookups = brush.presets().stream()
                           .map(token -> storage.activeStorage().presets().containerByName(player, token.text())
                                                .get(token.text().replaceAll("^g:", "")))
                           .toList();
        CompletableFuture.allOf(lookups.toArray(CompletableFuture[]::new))
                         .whenComplete((ignored, error) -> plugin().getServer().getScheduler().runTask(plugin(), () -> {
                             if (error != null) {
                                 handleCommandError(player, error);
                                 return;
                             }
                             apply(player, input, brush, lookups);
                         }));
    }

    private void apply(Player player, String input, LegacyBrush brush, List<CompletableFuture<Optional<Preset>>> lookups) {
        try {
            var presetSets = new ArrayList<SchematicSetBuilder>();
            for (var index = 0; index < lookups.size(); index++) {
                var preset = lookups.get(index).join();
                if (preset.isEmpty()) throw new ParseException("commands.brush.parse.error.preset", brush.presets().get(index));
                presetSets.addAll(preset.get().schematicSetsCopy());
            }
            sessions.startSession(player, mapper.map(player, brush, presetSets));
            bind.bind(player);
            if (brush.definitions().stream().anyMatch(definition -> definition.format() != null)) {
                messageSender().sendMessage(player, "commands.brush.parse.formatIgnored");
            }
        } catch (ParseException e) {
            sendError(player, input, e);
        } catch (CommandException e) {
            handleCommandError(player, e);
        }
    }

    private void sendError(Player player, String input, ParseException e) {
        var start = Math.min(e.start(), input.length());
        var end = Math.min(e.end(), input.length());
        var marked = start == end ? "_" : input.substring(start, end);
        var message = ILocalizer.isLocaleCode(e.getMessage()) ? ILocalizer.escape(e.getMessage()) : Escape.text(String.valueOf(e.getMessage()));
        messageSender().sendError(player, "%s%n<value>%s<warn><u>%s</u></warn><value>%s".formatted(
                message, Escape.text(input.substring(0, start)), Escape.text(marked), Escape.text(input.substring(end))));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull Player player, @NotNull String alias, @NotNull Arguments args) {
        var raw = RawArguments.of(args);
        return completion.complete(player, raw.length == 0 ? "" : raw[raw.length - 1]);
    }
}
