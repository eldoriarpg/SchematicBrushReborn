/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
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
    Location location();

    /**
     * Show changes to the player
     *
     * @param player player
     * @return the amount of send packets
     */
    int show(Player player);

    /**
     * Hide changes from the player
     *
     * @param player player
     * @return the amount of send packets
     */
    int hide(Player player);

    /**
     * The amount of changes
     *
     * @return changes
     */
    int size();

    /**
     * Hides the changes to the player based on new changes. This can be seen as an incremental update.
     *
     * @param player     player
     * @param newChanges new changes
     * @return the amount of send packets
     */
    int hide(Player player, Changes newChanges);

    /**
     * Changed blocks
     *
     * @return map of blocks
     */
    Map<Location, BlockData> changed();

    /**
     * Original blocks
     *
     * @return map of blocks
     */
    Map<Location, BlockData> original();

    /**
     * Shows the changes to the player based on old changes. This can be seen as an incremental update.
     *
     * @param player     player
     * @param oldChanges old changes
     * @return the amount of send packets
     */
    int show(Player player, Changes oldChanges);
}
