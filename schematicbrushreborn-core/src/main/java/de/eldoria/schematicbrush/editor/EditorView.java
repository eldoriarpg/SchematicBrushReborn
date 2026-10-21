/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.editor;

import org.bukkit.entity.Player;

/**
 * Shows the screens of the brush editor.
 */
public interface EditorView {
    /**
     * Shows the brush with its sets and placement modifiers.
     *
     * @param player player
     */
    void showBrush(Player player);

    /**
     * Shows the schematic sets of the brush.
     *
     * @param player player
     */
    void showSets(Player player);

    /**
     * Shows one schematic set.
     *
     * @param player player
     * @param id     index of the set
     */
    void showSet(Player player, int id);
}
