/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.entity.Player;

/**
 * A class to manage changes and send them to a player.
 */
public interface Changes {
    /**
     * Show changes to the player
     *
     * @param player player
     */
    void show(Player player);

    /**
     * Hide changes from the player
     *
     * @param player player
     */
    void hide(Player player);
}
