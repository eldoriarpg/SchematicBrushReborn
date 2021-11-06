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

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.SelectorProvider;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Class to register, save and parse brush settings.
 */
public interface BrushSettingsRegistry {
    /**
     * Registers a new selector.
     * <p>
     * This will also call {@link ConfigurationSerialization#registerClass(Class)}
     *
     * @param provider provider of the selector
     * @throws AlreadyRegisteredException when a selector with this name is already registered
     */
    void registerSelector(SelectorProvider provider);

    /**
     * Register a new schematic modifier.
     * <p>
     * This will also call {@link ConfigurationSerialization#registerClass(Class)}
     *
     * @param type     type of modifier
     * @param provider provider to add
     * @throws AlreadyRegisteredException when a modifier with this type and name is already registered
     */
    void registerSchematicModifier(SchematicModifier type, ModifierProvider provider);

    /**
     * Register a new schematic modifier.
     * <p>
     * This will also call {@link ConfigurationSerialization#registerClass(Class)}
     *
     * @param type     type of modifier
     * @param provider provider to add
     * @throws AlreadyRegisteredException when a modifier with this type and name is already registered
     */
    void registerPlacementModifier(PlacementModifier type, ModifierProvider provider);

    /**
     * Get the default selector. This selector will be the first registered selector.
     *
     * @return selector instance
     */
    Selector defaultSelector();

    /**
     * Get the default schematic modifier
     *
     * @return map containing all registered modifier types with one instance.
     */
    Map<SchematicModifier, Mutator<?>> defaultSchematicModifier();

    /**
     * Get the default placement modifier
     *
     * @return map containing all registered modifier types with one instance.
     */
    Map<PlacementModifier, Mutator<?>> defaultPlacementModifier();

    /**
     * Parse a selector from arguments
     *
     * @param args arguments to parse
     * @return the parsed selector
     * @throws CommandException if the arguments could not be parsed
     */
    Selector parseSelector(Arguments args) throws CommandException;

    /**
     * Parse a schematic modifier from arguments
     *
     * @param args arguments to parse
     * @return a pair containing the type and the parsed modifier
     * @throws CommandException if the arguments could not be parsed
     */
    Pair<SchematicModifier, Mutator<?>> parseSchematicModifier(Arguments args) throws CommandException;

    /**
     * Parse a placement modifier from arguments
     *
     * @param args arguments to parse
     * @return a pair containing the type and the parsed modifier
     * @throws CommandException if the arguments could not be parsed
     */
    Pair<PlacementModifier, Mutator<?>> parsePlacementModifier(Arguments args) throws CommandException;

    /**
     * Get registered selectors.
     *
     * @return unmodifiable list of selectors
     */
    List<SelectorProvider> selector();

    /**
     * Get registered schematic modifier
     *
     * @return unmodifiable map of all registered schematic modifier
     */
    Map<SchematicModifier, List<ModifierProvider>> schematicModifier();

    /**
     * Get registered placement modifier
     *
     * @return unmodifiable map of all registered placement modifier
     */
    Map<PlacementModifier, List<ModifierProvider>> placementModifier();

    /**
     * Complete selectors
     *
     * @param args   arguments to complete
     * @param player player which requested completion
     * @return list of possible values
     * @throws CommandException if the arguments are invalid
     */
    List<String> completeSelector(Arguments args, Player player) throws CommandException;

    /**
     * Complete placement modifier
     *
     * @param args arguments to complete
     * @return list of possible values
     * @throws CommandException if the arguments are invalid
     */
    List<String> completePlacementModifier(Arguments args) throws CommandException;

    /**
     * Complete schematic modifier
     *
     * @param args arguments to complete
     * @return list of possible values
     * @throws CommandException if the arguments are invalid
     */
    List<String> completeSchematicModifier(Arguments args) throws CommandException;
}
