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

public interface BrushHistory {
    /**
     * Get and remove the head of the history.
     *
     * @return current head of the history if present
     */
    Optional<Pair<SchematicSet, Schematic>> previous();

    /**
     * Push a new entry into the registry. The element will become the new head of the history if it is not the head already.
     *
     * @param set       set
     * @param schematic schematic
     */
    void push(SchematicSet set, Schematic schematic);

    /**
     * Push a new entry into the registry. The element will become the new head of the history if it is not the head already.
     *
     * @param pair set and schematic pair
     */
    default void push(Pair<SchematicSet, Schematic> pair) {
        push(pair.first, pair.second);
    }
}
