/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.schematics;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.util.Randomable;
import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@clazz")
public interface SchematicSelection extends Randomable, ConfigurationSerializable {
    Optional<Pair<SchematicSet, Schematic>> nextSchematic(SchematicBrush brush, boolean force);

    default Optional<Pair<SchematicSet, Schematic>> getDefaultSchem(SchematicBrush brush) {
        BrushSettings settings = brush.settings();
        return Optional.of(Pair.of(settings.schematicSets().get(0), settings.schematicSets().get(0).schematics().get(0)));
    }
}
