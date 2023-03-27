/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.config.util.Copyable;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Builder to build schematic sets.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@clazz")
public interface SchematicSetBuilder extends ConfigurationSerializable, Copyable {
    @Override
    @NotNull Map<String, Object> serialize();

    /**
     * Set the rotation of the brush.
     *
     * @param <T>      type of mutator
     * @param type     type of mutator
     * @param mutation rotation of the brush
     */
    <T extends Nameable> void withMutator(T type, Mutator<?> mutation);

    /**
     * Set the default modifier
     *
     * @param registry registry
     */
    void enforceDefaultModifier(BrushSettingsRegistry registry);

    /**
     * Set the weight of the brush.
     *
     * @param weight weight of the brush
     */
    void withWeight(int weight);

    /**
     * Build a sub brush.
     *
     * @return new sub brush instance with set values and default if not set.
     */
    SchematicSet build();

    /**
     * Selector
     *
     * @return selector
     */
    Selector selector();

    /**
     * Schematic modifier
     *
     * @return unmodifiable map
     */
    Map<? extends Nameable, Mutator<?>> schematicModifier();

    /**
     * Schematics
     *
     * @return schematics
     */
    Set<Schematic> schematics();

    /**
     * Weight
     *
     * @return weight
     */
    int weight();

    /**
     * Selector
     *
     * @param selector selector
     */
    void selector(Selector selector);

    /**
     * refresh all selected schematics
     *
     * @param player   player
     * @param registry registry
     */
    void refreshSchematics(Player player, SchematicRegistry registry);

    /**
     * Schematic count of set
     *
     * @return schematic count
     */
    int schematicCount();

    /**
     * Schematic set as interactable component
     *
     * @param player   player
     * @param registry registry
     * @param id       id
     * @return component
     */
    String interactComponent(Player player, BrushSettingsRegistry registry, int id);

    /**
     * Schematic set as component
     *
     * @return component
     */
    String infoComponent();

    /**
     * Copy the schematic set by creating a new instance and copying the entries by value to the new builder to allow independent modification
     *
     * @return new schematic set builder instande
     */
    SchematicSetBuilder copy();

    /**
     * Sets a modifier for this schematic set. Will override already present values
     *
     * @param modifier modifier name
     * @param mutator  mutator to set
     */
    void setModifier(Nameable modifier, Mutator<?> mutator);

    /**
     * Removes the modifier
     *
     * @param modifier modifier
     */
    void removeModifier(Nameable modifier);
}
