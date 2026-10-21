/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Computes the block changes that move a viewer from the preview it shows to the next one.
 * <p>
 * Blocks of the next preview are sent where the viewer does not show them yet. Blocks the viewer shows that are not
 * part of the next preview are reset to the live world. Sections whose client state is uncertain are sent in full; their
 * resets are sent as well, which is harmless when the client already holds the real blocks.
 */
final class PreviewDiff {
    private PreviewDiff() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Computes the changes between two previews.
     *
     * @param displayed    the preview the viewer shows
     * @param next         the preview the viewer should show
     * @param lostSections sections of {@code displayed} whose client state is uncertain
     * @param live         reads the current block of the world
     * @return map of block key to the block data to send
     */
    static Map<Long, BlockData> between(PreviewSnapshot displayed, PreviewSnapshot next, Set<Long> lostSections, LiveBlocks live) {
        var changes = new HashMap<Long, BlockData>();
        for (long section : next.sections()) {
            var shown = lostSections.contains(section) ? Map.<Long, BlockData>of() : displayed.section(section);
            next.section(section).forEach((block, data) -> {
                if (!data.equals(shown.get(block))) changes.put(block, data);
            });
        }
        for (long section : displayed.sections()) {
            var target = next.section(section);
            for (long block : displayed.section(section).keySet()) {
                if (target.containsKey(block)) continue;
                var current = live.at(block);
                if (current != null) changes.put(block, current);
            }
        }
        return changes;
    }

    /**
     * Reads blocks of the real world.
     */
    @FunctionalInterface
    interface LiveBlocks {
        /**
         * Reads the current block at a position.
         *
         * @param blockKey packed block position
         * @return the block data, or null if it can not be read without loading a chunk
         */
        @Nullable
        BlockData at(long blockKey);
    }
}
