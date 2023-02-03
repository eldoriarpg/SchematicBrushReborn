/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.schematics;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderedSelection implements SchematicSelection {

    public OrderedSelection() {
    }

    /**
     * Constructor required by {@link ConfigurationSerializable} in order to deserialize the object.
     */
    @SuppressWarnings("unused")
    public OrderedSelection(Map<String, Object> objectMap) {
    }

    @Override
    public Optional<Pair<SchematicSet, Schematic>> nextSchematic(SchematicBrush brush, boolean force) {
        Schematic currSchematic = brush.nextPaste().schematic();
        SchematicSet currSet = brush.nextPaste().schematicSet();
        List<Schematic> currSchematics = currSet.schematics();
        List<SchematicSet> schematicSets = brush.settings().schematicSets();
        var nextSet = currSet;
        var nextSchematic = currSchematic;

        if (currSchematics.indexOf(currSchematic) + 1 >= currSchematics.size()) {
            if (schematicSets.indexOf(currSet) + 1 >= schematicSets.size()) {
                nextSet = schematicSets.get(0);
            } else {
                nextSet = schematicSets.get(schematicSets.indexOf(currSet) + 1);
            }
            nextSchematic = nextSet.schematics().get(0);
        } else {
            nextSchematic = nextSet.schematics().get(currSchematics.indexOf(currSchematic) + 1);
        }

        return Optional.of(Pair.of(nextSet, nextSchematic));
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }
}
