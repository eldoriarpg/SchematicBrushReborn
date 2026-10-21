/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditHistoryTest {
    @Test
    void undoReturnsStatesInReverseOrder() {
        var history = new EditHistory<String>(10);
        history.record("a");
        history.record("b");

        assertEquals(Optional.of("b"), history.undo("c"));
        assertEquals(Optional.of("a"), history.undo("b"));
        assertEquals(Optional.empty(), history.undo("a"));
    }

    @Test
    void redoRestoresWhatWasUndone() {
        var history = new EditHistory<String>(10);
        history.record("a");
        history.undo("b");

        assertEquals(Optional.of("b"), history.redo("a"));
        assertEquals(Optional.of("a"), history.undo("b"));
    }

    @Test
    void newEditClearsRedo() {
        var history = new EditHistory<String>(10);
        history.record("a");
        history.undo("b");

        history.record("a");

        assertFalse(history.canRedo());
        assertEquals(Optional.empty(), history.redo("c"));
    }

    @Test
    void dropsOldestStatesBeyondTheLimit() {
        var history = new EditHistory<String>(2);
        history.record("a");
        history.record("b");
        history.record("c");

        assertEquals(Optional.of("c"), history.undo("d"));
        assertEquals(Optional.of("b"), history.undo("c"));
        assertTrue(history.undo("b").isEmpty());
    }
}
