/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.commands.brush;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * Undo and redo stacks of states. Recording a new state clears the redo stack; the oldest states are dropped beyond
 * the limit.
 *
 * @param <T> type of the recorded state
 */
final class EditHistory<T> {
    private final Deque<T> undo = new ArrayDeque<>();
    private final Deque<T> redo = new ArrayDeque<>();
    private final int limit;

    /**
     * Creates an empty history.
     *
     * @param limit the maximum amount of states that can be undone
     */
    EditHistory(int limit) {
        this.limit = limit;
    }

    /**
     * Records the state before an edit.
     *
     * @param before state before the edit
     */
    void record(T before) {
        push(undo, before);
        redo.clear();
    }

    /**
     * Steps back one edit.
     *
     * @param current the current state, which becomes redoable
     * @return the state to restore, empty if there is nothing to undo
     */
    Optional<T> undo(T current) {
        if (undo.isEmpty()) return Optional.empty();
        push(redo, current);
        return Optional.of(undo.pop());
    }

    /**
     * Steps forward one undone edit.
     *
     * @param current the current state, which becomes undoable
     * @return the state to restore, empty if there is nothing to redo
     */
    Optional<T> redo(T current) {
        if (redo.isEmpty()) return Optional.empty();
        push(undo, current);
        return Optional.of(redo.pop());
    }

    boolean canUndo() {
        return !undo.isEmpty();
    }

    boolean canRedo() {
        return !redo.isEmpty();
    }

    private void push(Deque<T> stack, T state) {
        stack.push(state);
        if (stack.size() > limit) {
            stack.removeLast();
        }
    }
}
