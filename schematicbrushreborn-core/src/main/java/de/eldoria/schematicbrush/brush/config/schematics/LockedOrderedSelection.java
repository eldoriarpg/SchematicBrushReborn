/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.schematics;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.BrushPaste;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Returns the same schematic unless it is prompted by force to draw a new one. In that case it will act like {@link OrderedSelection}.
 */
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
        BrushPaste next = brush.nextPaste();
        if (next == null) {
            return getDefaultSchem(brush);
        }
        Schematic currSchematic = next.schematic();
        SchematicSet currSet = next.schematicSet();
        if (!force) {
            return Optional.of(Pair.of(currSet, currSchematic));
        }
        return super.nextSchematic(brush, true);
    }
}
