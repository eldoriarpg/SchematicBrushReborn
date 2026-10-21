/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderImpl;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.brush.config.schematics.RandomSelection;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Escape;
import de.eldoria.schematicbrush.util.Permissions;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.brush.config.builder.BuildUtil.buildModifier;

public class Sessions {
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematicRegistry;
    private final Map<UUID, BrushSession> sessions = new HashMap<>();
    private final MessageSender messageSender;

    public Sessions(Plugin plugin, BrushSettingsRegistry registry, SchematicRegistry schematicRegistry) {
        this.registry = registry;
        this.schematicRegistry = schematicRegistry;
        messageSender = MessageSender.getPluginMessageSender(plugin);
    }

    public BrushBuilder getOrCreateSession(Player player) {
        return session(player).builder();
    }

    /**
     * Starts a new session. If the player holds a schematic brush, the session edits it live.
     *
     * @param player player
     */
    public void startSession(Player player) {
        sessions.put(player.getUniqueId(), open(player));
    }

    /**
     * Links the session to the schematic brush bound to the item in the main hand, so further edits apply to it.
     *
     * @param player player
     */
    public void linkHeldBrush(Player player) {
        var item = player.getInventory().getItemInMainHand().getType();
        WorldEditBrush.getSchematicBrush(player, item).ifPresent(brush -> session(player).link(item, brush));
    }

    /**
     * Changes the session as one undoable step and applies the result to the linked brush.
     *
     * @param player player
     * @param edit   change of the brush builder
     * @throws CommandException if the change is rejected; nothing is recorded then
     */
    public void edit(Player player, Edit edit) throws CommandException {
        var session = session(player);
        var before = session.builder().snapshot();
        edit.apply(session.builder());
        session.history().record(before);
        session.applyToLinked(player);
    }

    /**
     * Reverts the last edit.
     *
     * @param player player
     * @return true if an edit was reverted
     */
    public boolean undo(Player player) {
        var session = session(player);
        return restore(player, session, session.history().undo(session.builder().snapshot()));
    }

    /**
     * Repeats the last reverted edit.
     *
     * @param player player
     * @return true if an edit was repeated
     */
    public boolean redo(Player player) {
        var session = session(player);
        return restore(player, session, session.history().redo(session.builder().snapshot()));
    }

    private boolean restore(Player player, BrushSession session, Optional<BrushBuilderSnapshot> snapshot) {
        snapshot.ifPresent(state -> {
            session.builder().restore(state);
            session.applyToLinked(player);
        });
        return snapshot.isPresent();
    }

    private BrushSession session(Player player) {
        return sessions.computeIfAbsent(player.getUniqueId(), key -> open(player));
    }

    private BrushSession open(Player player) {
        var item = player.getInventory().getItemInMainHand().getType();
        return WorldEditBrush.getSchematicBrush(player, item)
                             .map(brush -> {
                                 var session = new BrushSession(brush.toBuilder(registry, schematicRegistry));
                                 session.link(item, brush);
                                 return session;
                             })
                             .orElseGet(() -> new BrushSession(new BrushBuilderImpl(player, new RandomSelection(), registry, schematicRegistry)));
    }

    public void showBrush(Player player) {
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
                .text(historyButton("undo", session(player).history().canUndo()))
                .space()
                .text(historyButton("redo", session(player).history().canRedo()));

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

    public void showSet(Player player, int id) {
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

        messageSender.sendMessage(player, MessageComposer.create().text(composer.build()).prependLines(20).build());
    }

    public void showSets(Player player) {
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

    /**
     * A change of the brush builder that may be rejected.
     */
    @FunctionalInterface
    public interface Edit {
        /**
         * Applies the change.
         *
         * @param builder builder of the session
         * @throws CommandException if the change is rejected
         */
        void apply(BrushBuilder builder) throws CommandException;
    }

    private static String historyButton(String action, boolean available) {
        if (available) {
            return "<click:run_command:'/sbr %s'><change>[<i18n:words.%s>]</click>".formatted(action, action);
        }
        return "<inactive>[<i18n:words.%s>]".formatted(action);
    }
}
