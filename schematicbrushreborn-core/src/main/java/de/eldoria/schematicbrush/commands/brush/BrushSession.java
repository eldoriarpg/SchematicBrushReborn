/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The brush a player edits in the editor, its undo history, and the bound brush the edits are applied to.
 * <p>
 * The link points at the brush bound to an item type. It is dropped as soon as that item carries another brush or none.
 */
final class BrushSession {
    private static final int HISTORY_LIMIT = 50;

    private final BrushBuilder builder;
    private final EditHistory<BrushBuilderSnapshot> history = new EditHistory<>(HISTORY_LIMIT);
    @Nullable
    private Material linkedItem;
    @Nullable
    private SchematicBrush linkedBrush;

    BrushSession(BrushBuilder builder) {
        this.builder = builder;
    }

    BrushBuilder builder() {
        return builder;
    }

    EditHistory<BrushBuilderSnapshot> history() {
        return history;
    }

    /**
     * Links the session to a bound brush.
     *
     * @param item  item type the brush is bound to
     * @param brush the bound brush
     */
    void link(Material item, SchematicBrush brush) {
        linkedItem = item;
        linkedBrush = brush;
    }

    /**
     * Applies the current state of the builder to the linked brush.
     *
     * @param player owner of the session
     */
    void applyToLinked(Player player) {
        if (linkedItem == null || linkedBrush == null) return;
        var bound = WorldEditBrush.getSchematicBrush(player, linkedItem);
        if (bound.isEmpty() || bound.get() != linkedBrush) {
            linkedItem = null;
            linkedBrush = null;
            return;
        }
        linkedBrush.settings(builder.buildSettings());
    }
}
