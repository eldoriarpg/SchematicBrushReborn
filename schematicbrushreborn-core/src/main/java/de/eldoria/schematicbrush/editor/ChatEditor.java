/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.editor;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.commands.brush.Sessions;
import de.eldoria.schematicbrush.util.Escape;
import de.eldoria.schematicbrush.util.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.brush.config.builder.BuildUtil.buildModifier;

/**
 * The brush editor as clickable chat messages that run or suggest the {@code /sbr} sub commands.
 */
public final class ChatEditor implements EditorView {
    private final Sessions sessions;
    private final BrushSettingsRegistry registry;
    private final MessageSender messageSender;

    public ChatEditor(Plugin plugin, Sessions sessions, BrushSettingsRegistry registry) {
        this.sessions = sessions;
        this.registry = registry;
        messageSender = MessageSender.getPluginMessageSender(plugin);
    }

    @Override
    public void showBrush(Player player) {
        var builder = sessions.getOrCreateSession(player);

        var selectors = builder.schematicSets().stream().map(set -> BuildUtil.renderProvider(set.selector())).collect(Collectors.joining("\n"));
        var composer = MessageComposer.create()
                                      .text("<heading>")
                                      .localeCode("commands.brush.sessions.menu")
                                      .newLine()
                                      .text("<heading>")
                                      .localeCode("words.schematicSets")
                                      .text(":")
                                      .space()
                                      .text("<hover:show_text:'%s'><value>%s <i18n:words.sets> </hover><change><click:run_command:'/sbr showsets'>[<i18n:words.change>]</click>",
                                              Escape.argument(selectors), builder.schematicSets().size());

        var mutatorMap = builder.placementModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : mutatorMap.entrySet()) {
            var registration = registry.getPlacementModifier(entry.getKey()).get();
            modifierStrings.add(buildModifier(player, "/sbr modify", "/sbr removebrushmodifier",
                    registration.modifier(), registration.mutators(), mutatorMap.get(entry.getKey())));
        }

        composer.newLine()
                .text(modifierStrings);

        var missing = registry.placementModifier().keySet().stream().filter(providers -> !mutatorMap.containsKey(providers))
                              .map(provider -> String.format("<click:run_command:'/sbr addbrushmodifier %s'><hover:show_text:'<neutral>%s'><change>[%s]</click>",
                                      provider.name(), provider.description(), provider.getLocalizedName()))
                              .toList();

        if (!missing.isEmpty()) {
            composer.newLine()
                    .text("<heading>")
                    .localeCode("commands.brush.sessions.addModifiers")
                    .text(": ")
                    .text(missing, " ");
        }

        composer.newLine()
                .text("<click:run_command:'/sbr bind'><add>[<i18n:words.bind>]</click>")
                .space()
                .text("<click:run_command:'/sbr clear'><remove>[<i18n:words.clear>]</click>")
                .space()
                .text(historyButton("undo", sessions.canUndo(player)))
                .space()
                .text(historyButton("redo", sessions.canRedo(player)));

        if (player.hasPermission(Permissions.BrushPreset.USE)) {
            composer.space().text("<click:suggest_command:'/sbr loadbrush '><add>[")
                    .localeCode("commands.brush.sessions.loadBrush")
                    .text("]</click>")
                    .space().text("<click:suggest_command:'/sbr savebrush '><change>[")
                    .localeCode("commands.brush.sessions.saveBrush")
                    .text("]</click>");
        }

        composer.prependLines(20);
        messageSender.sendMessage(player, composer.build());
    }

    @Override
    public void showSet(Player player, int id) {
        var builder = sessions.getOrCreateSession(player);
        var optSet = builder.getSchematicSet(id);
        if (optSet.isEmpty()) {
            messageSender.sendError(player, "error.invalidSetId");
            return;
        }

        var set = optSet.get();
        var interactComponent = set.interactComponent(player, registry, id);

        var composer = MessageComposer.create()
                                      .text(interactComponent)
                                      .newLine()
                                      .text("<click:run_command:'/sbr showsets'><change>[<i18n:words.back>]</click>");

        messageSender.sendMessage(player, MessageComposer.create().text(composer.build()).prependLines(20).build());
    }

    @Override
    public void showSets(Player player) {
        var builder = sessions.getOrCreateSession(player);
        var count = new AtomicInteger(0);

        var composer = MessageComposer.create()
                                      .text("<heading>Schematic Sets: <click:run_command:'/sbr addSet'><add>[<i18n:words.add>]</click>")
                                      .space().text("<click:run_command:'/sbr refreshSchematics session'><add>[")
                                      .localeCode("commands.brush.sessions.refreshSchematics")
                                      .text("]</click>");

        if (player.hasPermission(Permissions.Preset.USE)) {
            composer.space().text("<click:suggest_command:'/sbr addpreset '><add>[")
                    .localeCode("commands.brush.sessions.addPreset")
                    .text("]</click>");
        }

        var sets = builder.schematicSets().stream()
                          .map(set -> String.format("  <name><hover:show_text:'%s'>%s</hover> <change><click:run_command:'/sbr showSet %s'>[<i18n:words.edit>]</click> <remove><click:run_command:'/sbr removeSet %s'>[<i18n:words.remove>]</click>",
                                  Escape.argument(set.infoComponent()), BuildUtil.renderProvider(set.selector()), count.get(), count.getAndIncrement()))
                          .collect(Collectors.joining("\n"));

        composer.newLine().text(sets);

        if (player.hasPermission(Permissions.Preset.USE)) {
            composer.newLine().text("<click:suggest_command:'/sbr savepreset '><change>[").localeCode("commands.brush.sessions.savePreset")
                    .text("]</click>");
        }

        composer.newLine().text("<click:run_command:'/sbr show'><change>[<i18n:words.back>]</click>");

        messageSender.sendMessage(player, composer.prependLines(20).build());
    }

    private static String historyButton(String action, boolean available) {
        if (available) {
            return "<click:run_command:'/sbr %s'><change>[<i18n:words.%s>]</click>".formatted(action, action);
        }
        return "<inactive>[<i18n:words.%s>]".formatted(action);
    }
}
