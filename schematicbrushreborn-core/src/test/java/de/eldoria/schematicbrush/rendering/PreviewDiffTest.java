/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PreviewDiffTest {
    private final BlockData stone = mock(BlockData.class);
    private final BlockData dirt = mock(BlockData.class);
    private final BlockData air = mock(BlockData.class);
    private final Map<Long, BlockData> world = new HashMap<>();
    private final PreviewDiff.LiveBlocks live = world::get;

    @Test
    void sendsEveryBlockOfANewPreview() {
        var next = snapshot(Map.of(BlockKey.of(0, 0, 0), stone, BlockKey.of(40, 70, 40), dirt));

        var changes = PreviewDiff.between(PreviewSnapshot.empty(), next, Set.of(), live);

        assertEquals(Map.of(BlockKey.of(0, 0, 0), stone, BlockKey.of(40, 70, 40), dirt), changes);
    }

    @Test
    void doesNotResendBlocksTheViewerAlreadyShows() {
        var shown = snapshot(Map.of(BlockKey.of(0, 0, 0), stone, BlockKey.of(1, 0, 0), stone));
        var next = snapshot(Map.of(BlockKey.of(0, 0, 0), stone, BlockKey.of(1, 0, 0), dirt));

        var changes = PreviewDiff.between(shown, next, Set.of(), live);

        assertEquals(Map.of(BlockKey.of(1, 0, 0), dirt), changes);
    }

    @Test
    void resetsBlocksThatLeaveThePreviewToTheLiveWorld() {
        world.put(BlockKey.of(5, 5, 5), air);
        var shown = snapshot(Map.of(BlockKey.of(5, 5, 5), stone));

        var changes = PreviewDiff.between(shown, PreviewSnapshot.empty(), Set.of(), live);

        assertEquals(Map.of(BlockKey.of(5, 5, 5), air), changes);
    }

    @Test
    void skipsResetsInUnloadedChunks() {
        var shown = snapshot(Map.of(BlockKey.of(5, 5, 5), stone));

        var changes = PreviewDiff.between(shown, PreviewSnapshot.empty(), Set.of(), live);

        assertTrue(changes.isEmpty());
    }

    @Test
    void sendsLostSectionsInFullAndStillResetsLeavingBlocks() {
        world.put(BlockKey.of(2, 0, 0), air);
        var shown = snapshot(Map.of(BlockKey.of(0, 0, 0), stone, BlockKey.of(2, 0, 0), stone, BlockKey.of(40, 0, 0), stone));
        var next = snapshot(Map.of(BlockKey.of(0, 0, 0), stone, BlockKey.of(40, 0, 0), stone));
        var lost = Set.of(BlockKey.section(0, 0, 0));

        var changes = PreviewDiff.between(shown, next, lost, live);

        assertEquals(Map.of(BlockKey.of(0, 0, 0), stone, BlockKey.of(2, 0, 0), air), changes);
    }

    private static PreviewSnapshot snapshot(Map<Long, BlockData> blocks) {
        var builder = PreviewSnapshot.builder(new Location(null, 0, 0, 0));
        blocks.forEach((key, data) -> builder.add(BlockKey.x(key), BlockKey.y(key), BlockKey.z(key), data));
        return builder.build();
    }
}
