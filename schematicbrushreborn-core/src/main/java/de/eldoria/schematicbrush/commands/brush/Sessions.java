/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.messageblocker.blocker.MessageBlocker;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderImpl;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Colors;
import de.eldoria.schematicbrush.util.Permissions;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final BukkitAudiences audiences;
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematicRegistry;
    private final MessageBlocker messageBlocker;
    private final ILocalizer localizer;
    private final Map<UUID, BrushBuilder> session = new HashMap<>();

    public Sessions(Plugin plugin, BrushSettingsRegistry registry, SchematicRegistry schematicRegistry, MessageBlocker messageBlocker) {
        this.registry = registry;
        audiences = BukkitAudiences.create(plugin);
        this.schematicRegistry = schematicRegistry;
        this.messageBlocker = messageBlocker;
        this.localizer = ILocalizer.getPluginLocalizer(plugin);
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
                .orElse(new BrushBuilderImpl(player, registry, schematicRegistry));
    }

    public void showBrush(Player player) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);

        var selectors = builder.schematicSets().stream().map(set -> BuildUtil.renderProvider(set.selector())).collect(Collectors.joining("\n"));

        var composer = MessageComposer.create()
                .text("<%s>Schematic Brush Menu", Colors.HEADING)
                .newLine()
                .text("<%s>Schematic Sets: <hover:show_text:'%s'><%s>%s Sets </hover><%s><click:run_command:'/sbr showsets'>[Change]</click>",
                        Colors.HEADING, selectors, Colors.VALUE, builder.schematicSets().size(), Colors.CHANGE);

        var mutatorMap = builder.placementModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : mutatorMap.entrySet()) {
            var registration = registry.getPlacementModifier(entry.getKey()).get();
            modifierStrings.add(buildModifier(localizer, player, "/sbr modify", "/sbr removebrushmodifier",
                    registration.modifier(), registration.mutators(), mutatorMap.get(entry.getKey())));
        }

        composer.newLine()
                .text(modifierStrings);

        var missing = registry.placementModifier().keySet().stream().filter(providers -> !mutatorMap.containsKey(providers))
                .map(provider -> String.format("<click:run_command:'/sbr addbrushmodifier %s'><hover:show_text:'<%s>%s'><%s>[%s]</click>",
                        provider.name(), Colors.NEUTRAL, localizer.localize(provider.description()), Colors.CHANGE, provider.name()))
                .toList();

        if (!missing.isEmpty()) {
            composer.newLine()
                    .text("<%s>Add Modifiers: ", Colors.HEADING)
                    .text(missing, " ");
        }

        composer.newLine()
                .text("<click:run_command:'/sbr bind'><%s>[Bind]</click>", Colors.ADD)
                .space()
                .text("<click:run_command:'/sbr clear'><%s>[Clear]</click>", Colors.REMOVE);

        if (player.hasPermission(Permissions.BrushPreset.USE)) {
            composer.space().text("<click:suggest_command:'/sbr loadbrush '><%s>[Load Brush]</click>", Colors.ADD)
                    .space().text("<click:suggest_command:'/sbr savebrush '><%s>[Save Brush]</click>", Colors.CHANGE);
        }

        composer.prependLines(20);
        messageBlocker.ifEnabled(composer, mess -> mess.newLine().text("<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.deserialize(composer.build()));
    }

    public void showSet(Player player, int id) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);
        var optSet = builder.getSchematicSet(id);
        if (optSet.isEmpty()) {
            audiences.player(player).sendMessage(miniMessage.deserialize("[SB] Invalid set."));
            return;
        }

        var set = optSet.get();
        var interactComponent = set.interactComponent(player, registry, id, localizer);

        var composer = MessageComposer.create()
                .text(interactComponent)
                .newLine()
                .text("<click:run_command:'/sbr showsets'><%s>[Back]</click>", Colors.CHANGE);

        messageBlocker.ifEnabled(() ->  composer.newLine().text("<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.deserialize(MessageComposer.create().text(composer.build()).prependLines(20).build()));
    }

    public void showSets(Player player) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);
        var count = new AtomicInteger(0);

        var composer = MessageComposer.create()
                .text("<%s>Schematic Sets: <click:run_command:'/sbr addSet'><%s>[Add]</click>", Colors.HEADING, Colors.ADD)
                .space().text("<click:run_command:'/sbr refreshSchematics session'><%s>[Refresh Schematics]</click>", Colors.ADD);

        if (player.hasPermission(Permissions.Preset.USE)) {
            composer.space().text("<click:suggest_command:'/sbr addpreset '><%s>[Add Preset]</click>", Colors.ADD);
        }

        var sets = builder.schematicSets().stream()
                .map(set -> String.format("  <%s><hover:show_text:'%s'>%s</hover> <%s><click:run_command:'/sbr showSet %s'>[Edit]</click> <%s><click:run_command:'/sbr removeSet %s'>[Remove]</click>",
                        Colors.NAME, set.infoComponent(localizer), BuildUtil.renderProvider(set.selector()), Colors.CHANGE, count.get(), Colors.REMOVE, count.getAndIncrement()))
                .collect(Collectors.joining("\n"));

        composer.newLine().text(sets);

        if (player.hasPermission(Permissions.Preset.USE)) {
            composer.newLine().text("<click:suggest_command:'/sbr savepreset '><%s>[Save Preset]</click>", Colors.CHANGE);
        }

        composer.newLine().text("<click:run_command:'/sbr show'><%s>[Back]</click>", Colors.CHANGE);

        messageBlocker.ifEnabled(composer, mess -> mess.newLine().text("<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");

        audiences.player(player).sendMessage(miniMessage.deserialize(composer.prependLines(20).build()));
    }

    public void setSession(Player player, BrushBuilder load) {
        session.put(player.getUniqueId(), load);
    }
}
