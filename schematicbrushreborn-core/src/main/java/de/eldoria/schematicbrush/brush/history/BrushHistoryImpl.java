/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.history;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.util.Optional;
import java.util.Stack;

public class BrushHistoryImpl implements BrushHistory {
    private final Stack<Pair<SchematicSet, Schematic>> history = new Stack<>();
    private final int size;

    public BrushHistoryImpl(int size) {
        this.size = size;
    }

    @Override
    public Optional<Pair<SchematicSet, Schematic>> previous() {
        if (history.empty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(history.pop());
    }

    @Override
    public void push(SchematicSet set, Schematic schematic) {
        // Only push if the new item is different to the head of the stack
        if (!history.empty() && history.peek().equals(Pair.of(set, schematic))) return;
        history.push(Pair.of(set, schematic));
        // keep size
        if (history.size() >= size) history.removeElementAt(history.size() - 1);
    }
}
