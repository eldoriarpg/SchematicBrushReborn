/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Represents a selector.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@clazz")
public interface Selector extends ConfigurationSerializable, ComponentProvider {
    /**
     * Select matching schematics from a cache in the registry
     *
     * @param player   player which wants to select schematics
     * @param registry registry instance
     * @return set of schematics
     */
    Set<Schematic> select(Player player, SchematicRegistry registry);
}
