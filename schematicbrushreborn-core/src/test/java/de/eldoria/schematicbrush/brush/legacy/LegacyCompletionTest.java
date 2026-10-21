/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyCompletionTest {
    private final LegacyCompletion completion = new LegacyCompletion(null, null, null, "/");

    @Test
    void completesOptionNames() {
        assertEquals(List.of("-place:"), completion.complete(null, "-pl"));
    }

    @Test
    void completesPlacements() {
        assertEquals(List.of("-place:drop"), completion.complete(null, "-place:dr"));
    }

    @Test
    void completesRotationsAndFlipsAfterAt() {
        assertEquals(List.of("oak@90"), completion.complete(null, "oak@9"));
        assertTrue(completion.complete(null, "oak@").containsAll(List.of("oak@0", "oak@270", "oak@*", "oak@N")));
        assertTrue(completion.complete(null, "oak@90").containsAll(List.of("oak@90", "oak@90N", "oak@90*", "oak@90,")));
        assertEquals(List.of("oak@90,180"), completion.complete(null, "oak@90,18"));
    }

    @Test
    void hintsNumbers() {
        assertEquals(List.of("oak:<1-100>"), completion.complete(null, "oak:"));
        assertEquals(List.of("oak^<offset>"), completion.complete(null, "oak^"));
        assertEquals(List.of("-yoff:<offset>"), completion.complete(null, "-yoff:"));
    }
}
