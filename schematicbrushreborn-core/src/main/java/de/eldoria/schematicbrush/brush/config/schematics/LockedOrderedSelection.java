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

import java.util.Map;
import java.util.Optional;

public class LockedOrderedSelection extends OrderedSelection {

    public LockedOrderedSelection() {
    }

    /**
     * Constructor required by {@link ConfigurationSerializable} in order to deserialize the object.
     */
    @SuppressWarnings("unused")
    public LockedOrderedSelection(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }

    @Override
    public Optional<Pair<SchematicSet, Schematic>> nextSchematic(SchematicBrush brush, boolean force) {
        Schematic currSchematic = brush.nextPaste().schematic();
        SchematicSet currSet = brush.nextPaste().schematicSet();
        if (!force) {
            return Optional.of(Pair.of(currSet, currSchematic));
        }
        return super.nextSchematic(brush, true);
    }
}
