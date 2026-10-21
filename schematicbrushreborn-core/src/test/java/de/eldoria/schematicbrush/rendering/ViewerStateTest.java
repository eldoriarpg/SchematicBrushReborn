/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ViewerStateTest {
    private final BlockData stone = mock(BlockData.class);
    private final PreviewSnapshot preview = PreviewSnapshot.builder(new Location(null, 0, 0, 0))
                                                           .add(1, 1, 1, stone)
                                                           .add(1, 100, 1, stone)
                                                           .add(20, 1, 1, stone)
                                                           .build();

    @Test
    void loseChunkMarksEverySectionOfThatChunk() {
        var viewer = new ViewerState();
        viewer.shows(preview);

        viewer.loseChunk(0, 0);

        assertEquals(Set.of(BlockKey.section(1, 1, 1), BlockKey.section(1, 100, 1)), viewer.lostSections());
    }

    @Test
    void loseSectionIgnoresSectionsThatAreNotDisplayed() {
        var viewer = new ViewerState();
        viewer.shows(preview);

        viewer.loseSection(BlockKey.section(500, 1, 500));

        assertTrue(viewer.lostSections().isEmpty());
    }

    @Test
    void showingAPreviewClearsLostSections() {
        var viewer = new ViewerState();
        viewer.shows(preview);
        viewer.loseAll();

        viewer.shows(preview);

        assertTrue(viewer.lostSections().isEmpty());
    }

    @Test
    void snapshotCountsEachPositionOnce() {
        var snapshot = PreviewSnapshot.builder(new Location(null, 0, 0, 0))
                                      .add(1, 1, 1, stone)
                                      .add(1, 1, 1, stone)
                                      .build();

        assertEquals(1, snapshot.size());
    }
}
