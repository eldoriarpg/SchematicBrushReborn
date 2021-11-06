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

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builder to build brushes.
 */
public interface BrushBuilder {
    /**
     * Get the schematic set builder for the set with the id
     *
     * @param id id
     * @return set if the id exists
     */
    Optional<SchematicSetBuilder> getSchematicSet(int id);

    /**
     * Remove the set with the id
     *
     * @param id id
     * @return true if the set with the id was removed
     */
    boolean removeSchematicSet(int id);

    /**
     * Create a new schematic set
     *
     * @return id of the created schematic set
     */
    int createSchematicSet();

    /**
     * Sets the placement modifier
     *
     * @param type     type
     * @param provider provider
     */
    void setPlacementModifier(PlacementModifier type, Mutator<?> provider);

    /**
     * Build the schematic brush
     *
     * @param plugin plugin
     * @param owner  brush owner
     * @return new schematic brush instance
     */
    SchematicBrush build(Plugin plugin, Player owner);

    /**
     * Add a new schematic set builder
     *
     * @param schematicSetBuilder builder to add
     */
    void addSchematicSet(SchematicSetBuilder schematicSetBuilder);

    /**
     * Get the registered schematic set builder
     *
     * @return unmodifiable list of schematic sets
     */
    List<SchematicSetBuilder> schematicSets();

    /**
     * Get the current placement modifier
     *
     * @return unmodifiable map of the placement modifier
     */
    Map<PlacementModifier, Mutator<?>> placementModifier();

    /**
     * Reset all modifier to default and clear schematic sets.
     */
    void clear();

    /**
     * Get the schematic count of the brush
     *
     * @return schematic count
     */
    int getSchematicCount();

    /**
     * Reload all schematics in the brush
     */
    void refresh();
}
