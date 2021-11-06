/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
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
public interface SchematicSetBuilder extends ConfigurationSerializable {
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
     *
     * @param player
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

    SchematicSetBuilder clone();
}
