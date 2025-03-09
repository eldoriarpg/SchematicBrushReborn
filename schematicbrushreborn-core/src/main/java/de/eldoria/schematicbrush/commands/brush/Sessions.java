/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderImpl;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.brush.config.schematics.RandomSelection;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Permissions;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.brush.config.builder.BuildUtil.buildModifier;

public class Sessions {
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematicRegistry;
    private final MessageBlocker messageBlocker;
    private final Map<UUID, BrushBuilder> session = new HashMap<>();
    private final MessageSender messageSender;

    public Sessions(Plugin plugin, BrushSettingsRegistry registry, SchematicRegistry schematicRegistry, MessageBlocker messageBlocker) {
        this.registry = registry;
        this.schematicRegistry = schematicRegistry;
        this.messageBlocker = messageBlocker;
        messageSender = MessageSender.getPluginMessageSender(plugin);
    }

    public BrushBuilder getOrCreateSession(Player player) {
        return session.computeIfAbsent(player.getUniqueId(), key -> getOrCreateBuilder(player));
    }

    public void startSession(Player player) {
        session.put(player.getUniqueId(), getOrCreateBuilder(player));
    }

    private BrushBuilder getOrCreateBuilder(Player player) {
        return WorldEditBrush.getSchematicBrush(player)
                             .map(brush -> brush.toBuilder(registry, schematicRegistry))
                             .orElse(new BrushBuilderImpl(player, new RandomSelection(), registry, schematicRegistry));
    }

    public void showBrush(Player player) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);

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
                                              selectors, builder.schematicSets().size());

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
                .text("<click:run_command:'/sbr clear'><remove>[<i18n:words.clear>]</click>");

        if (player.hasPermission(Permissions.BrushPreset.USE)) {
            composer.space().text("<click:suggest_command:'/sbr loadbrush '><add>[")
                    .localeCode("commands.brush.sessions.loadBrush")
                    .text("]</click>")
                    .space().text("<click:suggest_command:'/sbr savebrush '><change>[")
                    .localeCode("commands.brush.sessions.saveBrush")
                    .text("]</click>");
        }

        composer.prependLines(20);
        messageBlocker.ifEnabled(composer, mess -> mess.newLine().text("<click:run_command:'/sbrs chatblock false'><remove>[x]</click>"));
        messageBlocker.announce(player, "[x]");
        messageSender.sendMessage(player, composer.build());
    }

    public void showSet(Player player, int id) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);
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

        messageBlocker.ifEnabled(() -> composer.newLine().text("<click:run_command:'/sbrs chatblock false'><remove>[x]</click>"));
        messageBlocker.announce(player, "[x]");
        messageSender.sendMessage(player, MessageComposer.create().text(composer.build()).prependLines(20).build());
    }

    public void showSets(Player player) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);
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
                                  set.infoComponent(), BuildUtil.renderProvider(set.selector()), count.get(), count.getAndIncrement()))
                          .collect(Collectors.joining("\n"));

        composer.newLine().text(sets);

        if (player.hasPermission(Permissions.Preset.USE)) {
            composer.newLine().text("<click:suggest_command:'/sbr savepreset '><change>[").localeCode("commands.brush.sessions.savePreset")
                    .text("]</click>");
        }

        composer.newLine().text("<click:run_command:'/sbr show'><change>[<i18n:words.back>]</click>");

        messageBlocker.ifEnabled(composer, mess -> mess.newLine().text("<click:run_command:'/sbrs chatblock false'><remove>[x]</click>"));
        messageBlocker.announce(player, "[x]");

        messageSender.sendMessage(player, composer.prependLines(20).build());
    }

    public void setSession(Player player, BrushBuilder load) {
        session.put(player.getUniqueId(), load);
    }
}
