/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.Map;

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

    /**
     * The amount of changes
     * @return changes
     */
    int size();

    void hide(Player player, Changes newChanges);

    Map<Location, BlockData> changed();

    Map<Location, BlockData> original();

    void show(Player player, Changes oldChanges);
}
