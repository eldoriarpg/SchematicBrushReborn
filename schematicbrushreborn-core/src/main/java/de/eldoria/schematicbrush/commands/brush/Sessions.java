/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderImpl;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.brush.config.schematics.RandomSelection;
import de.eldoria.schematicbrush.config.sections.EditorType;
import de.eldoria.schematicbrush.editor.ChatEditor;
import de.eldoria.schematicbrush.editor.DialogEditor;
import de.eldoria.schematicbrush.editor.EditorPreferences;
import de.eldoria.schematicbrush.editor.EditorView;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Sessions {
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematicRegistry;
    private final Map<UUID, BrushSession> sessions = new HashMap<>();
    private final EditorPreferences preferences;
    private final EditorView chat;
    private final EditorView dialog;

    public Sessions(Plugin plugin, BrushSettingsRegistry registry, SchematicRegistry schematicRegistry, EditorPreferences preferences) {
        this.registry = registry;
        this.schematicRegistry = schematicRegistry;
        this.preferences = preferences;
        chat = new ChatEditor(plugin, this, registry);
        dialog = new DialogEditor(plugin, this, registry, schematicRegistry);
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
     * Starts a new session editing the given brush.
     *
     * @param player  player
     * @param builder brush to edit
     */
    public void startSession(Player player, BrushBuilder builder) {
        sessions.put(player.getUniqueId(), new BrushSession(builder));
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

    /**
     * Shows the brush in the editor the player uses.
     *
     * @param player player
     */
    public void showBrush(Player player) {
        view(player).showBrush(player);
    }

    /**
     * Shows the schematic sets in the editor the player uses.
     *
     * @param player player
     */
    public void showSets(Player player) {
        view(player).showSets(player);
    }

    /**
     * Shows a schematic set in the editor the player uses.
     *
     * @param player player
     * @param id     index of the set
     */
    public void showSet(Player player, int id) {
        view(player).showSet(player, id);
    }

    /**
     * Checks whether the player has an edit to revert.
     *
     * @param player player
     * @return true if {@link #undo(Player)} would revert an edit
     */
    public boolean canUndo(Player player) {
        return session(player).history().canUndo();
    }

    /**
     * Checks whether the player has a reverted edit to repeat.
     *
     * @param player player
     * @return true if {@link #redo(Player)} would repeat an edit
     */
    public boolean canRedo(Player player) {
        return session(player).history().canRedo();
    }

    private EditorView view(Player player) {
        return preferences.editor(player) == EditorType.DIALOG ? dialog : chat;
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
}
