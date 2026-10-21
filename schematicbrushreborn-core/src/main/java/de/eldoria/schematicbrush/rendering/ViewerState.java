/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import java.util.HashSet;
import java.util.Set;

/**
 * What one viewer currently shows of a preview, and which of those sections may differ on its client.
 */
final class ViewerState {
    private final Set<Long> lostSections = new HashSet<>();
    private PreviewSnapshot displayed = PreviewSnapshot.empty();

    PreviewSnapshot displayed() {
        return displayed;
    }

    Set<Long> lostSections() {
        return lostSections;
    }

    /**
     * Records that the client now shows the snapshot completely.
     *
     * @param snapshot the snapshot that was sent
     */
    void shows(PreviewSnapshot snapshot) {
        displayed = snapshot;
        lostSections.clear();
    }

    /**
     * Marks every displayed section as lost, so the next update sends it in full.
     */
    void loseAll() {
        lostSections.addAll(displayed.sections());
    }

    /**
     * Marks the displayed sections of a chunk as lost.
     *
     * @param chunkX chunk x coordinate
     * @param chunkZ chunk z coordinate
     */
    void loseChunk(int chunkX, int chunkZ) {
        for (long section : displayed.sections()) {
            if (BlockKey.x(section) == chunkX && BlockKey.z(section) == chunkZ) {
                lostSections.add(section);
            }
        }
    }

    /**
     * Marks a section as lost if it is displayed.
     *
     * @param sectionKey packed section key
     */
    void loseSection(long sectionKey) {
        if (displayed.sections().contains(sectionKey)) {
            lostSections.add(sectionKey);
        }
    }

    /**
     * Drops the displayed preview without resetting it, for when the client discarded all chunks.
     */
    void forget() {
        shows(PreviewSnapshot.empty());
    }
}
