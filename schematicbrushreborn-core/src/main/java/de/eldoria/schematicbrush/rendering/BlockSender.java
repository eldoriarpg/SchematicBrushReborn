/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import io.papermc.paper.math.Position;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends fake blocks to a client. Paper groups the blocks into one packet per 16x16x16 section.
 */
final class BlockSender {
    private BlockSender() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Sends block changes to a player.
     *
     * @param player  receiving player
     * @param changes map of block key to block data
     */
    static void send(Player player, Map<Long, BlockData> changes) {
        if (changes.isEmpty()) return;
        var blocks = new HashMap<Position, BlockData>(changes.size());
        changes.forEach((key, data) -> blocks.put(Position.block(BlockKey.x(key), BlockKey.y(key), BlockKey.z(key)), data));
        player.sendMultiBlockChange(blocks);
    }
}
