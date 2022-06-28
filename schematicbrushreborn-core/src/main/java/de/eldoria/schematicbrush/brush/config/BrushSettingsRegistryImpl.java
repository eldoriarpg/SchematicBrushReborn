/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.command.util.CommandAssertions;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.modifier.BaseModifier;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.SelectorProvider;
import de.eldoria.schematicbrush.brush.config.provider.SettingProvider;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Registry to register brush settings
 */
public class BrushSettingsRegistryImpl implements BrushSettingsRegistry {
    private final List<SelectorProvider> selector = new ArrayList<>();
    private final Map<SchematicModifier, List<ModifierProvider>> schematicModifier = new LinkedHashMap<>();
    private final Map<PlacementModifier, List<ModifierProvider>> placementModifier = new LinkedHashMap<>();

    /**
     * Registers a new selector.
     * <p>
     * This will also call {@link ConfigurationSerialization#registerClass(Class)}
     *
     * @param provider provider of the selector
     * @throws AlreadyRegisteredException when a selector with this name is already registered
     */
    @Override
    public void registerSelector(SelectorProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        if (selector.contains(provider)) {
            throw new AlreadyRegisteredException(provider);
        }
        selector.add(provider);
    }

    /**
     * Register a new schematic modifier.
     * <p>
     * This will also call {@link ConfigurationSerialization#registerClass(Class)}
     *
     * @param type     type of modifier
     * @param provider provider to add
     * @throws AlreadyRegisteredException when a modifier with this type and name is already registered
     */
    @Override
    public void registerSchematicModifier(SchematicModifier type, ModifierProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        if (schematicModifier.containsKey(type) && schematicModifier.get(type).contains(provider)) {
            throw new AlreadyRegisteredException(type, provider);
        }
        schematicModifier.computeIfAbsent(type, key -> new ArrayList<>()).add(provider);
    }

    /**
     * Register a new schematic modifier.
     * <p>
     * This will also call {@link ConfigurationSerialization#registerClass(Class)}
     *
     * @param type     type of modifier
     * @param provider provider to add
     * @throws AlreadyRegisteredException when a modifier with this type and name is already registered
     */
    @Override
    public void registerPlacementModifier(PlacementModifier type, ModifierProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        if (placementModifier.containsKey(type) && placementModifier.get(type).contains(provider)) {
            throw new AlreadyRegisteredException(type, provider);
        }
        placementModifier.computeIfAbsent(type, key -> new ArrayList<>()).add(provider);
    }

    /**
     * Get the default selector. This selector will be the first registered selector.
     *
     * @return selector instance
     */
    @Override
    public Selector defaultSelector() {
        return selector.get(0).defaultSetting();
    }

    /**
     * Get the default schematic modifier
     *
     * @return map containing all registered modifier types with one instance.
     */
    @Override
    public Map<SchematicModifier, Mutator<?>> defaultSchematicModifier() {
        return getDefaultMap(schematicModifier);
    }

    /**
     * Get the default placement modifier
     *
     * @return map containing all registered modifier types with one instance.
     */
    @Override
    public Map<PlacementModifier, Mutator<?>> defaultPlacementModifier() {
        return getDefaultMap(placementModifier);
    }

    private <T extends BaseModifier> Map<T, Mutator<?>> getDefaultMap(Map<T, List<ModifierProvider>> map) {
        return map
                .entrySet()
                .stream()
                .filter(e -> e.getKey().required())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        (e) -> e.getValue().get(0).defaultSetting()));
    }

    // Parsing

    /**
     * Parse a selector from arguments
     *
     * @param args arguments to parse
     * @return the parsed selector
     * @throws CommandException if the arguments could not be parsed
     */
    @Override
    public Selector parseSelector(Arguments args) throws CommandException {
        var provider = getSettingProvider(args, selector);
        CommandAssertions.invalidArguments(args.subArguments(), provider.arguments());
        return provider.parse(args.subArguments());
    }

    /**
     * Parse a schematic modifier from arguments
     *
     * @param args arguments to parse
     * @return a pair containing the type and the parsed modifier
     * @throws CommandException if the arguments could not be parsed
     */
    @Override
    public Pair<SchematicModifier, Mutator<?>> parseSchematicModifier(Arguments args) throws CommandException {
        var provider = getProvider(args, schematicModifier);
        var subArguments = args.subArguments().subArguments();
        CommandAssertions.invalidArguments(subArguments, provider.second.arguments());
        return Pair.of(provider.first, provider.second.parse(subArguments));
    }

    /**
     * Parse a placement modifier from arguments
     *
     * @param args arguments to parse
     * @return a pair containing the type and the parsed modifier
     * @throws CommandException if the arguments could not be parsed
     */
    @Override
    public Pair<PlacementModifier, Mutator<?>> parsePlacementModifier(Arguments args) throws CommandException {
        var provider = getProvider(args, placementModifier);
        var subArguments = args.subArguments().subArguments();
        CommandAssertions.invalidArguments(subArguments, provider.second.arguments());
        return Pair.of(provider.first, provider.second.parse(subArguments));
    }

    /**
     * Get registered selectors.
     *
     * @return unmodifiable list of selectors
     */
    @Override
    public List<SelectorProvider> selector() {
        return Collections.unmodifiableList(selector);
    }

    /**
     * Get registered schematic modifier
     *
     * @return unmodifiable map of all registered schematic modifier
     */
    @Override
    public Map<SchematicModifier, List<ModifierProvider>> schematicModifier() {
        return Collections.unmodifiableMap(schematicModifier);
    }

    /**
     * Get registered placement modifier
     *
     * @return unmodifiable map of all registered placement modifier
     */
    @Override
    public Map<PlacementModifier, List<ModifierProvider>> placementModifier() {
        return Collections.unmodifiableMap(placementModifier);
    }

    @Override
    public Optional<PlacementModifierRegistration> getPlacementModifier(String name) {
        return placementModifier.keySet().stream().filter(modifier -> modifier.name().equalsIgnoreCase(name)).findFirst()
                .map(modifier -> new PlacementModifierRegistrationImpl(placementModifier.get(modifier), modifier));
    }

    @Override
    public Optional<SchematicModifierRegistration> getSchematicModifier(String name) {
        return schematicModifier.keySet().stream().filter(modifier -> modifier.name().equalsIgnoreCase(name)).findFirst()
                .map(modifier -> new SchematicModifierRegistrationImpl(schematicModifier.get(modifier), modifier));
    }

    // Tab completion

    /**
     * Complete selectors
     *
     * @param args   arguments to complete
     * @param player player which requested completion
     * @return list of possible values
     * @throws CommandException if the arguments are invalid
     */
    @Override
    public List<String> completeSelector(Arguments args, Player player) throws CommandException {
        if (args.size() == 1) {
            return TabCompleteUtil.complete(args.asString(0), selector.stream().map(SettingProvider::name));
        }
        return getSettingProvider(args, selector).complete(args.subArguments(), player);
    }

    /**
     * Complete placement modifier
     *
     * @param args arguments to complete
     * @return list of possible values
     * @throws CommandException if the arguments are invalid
     */
    @Override
    public List<String> completePlacementModifier(Arguments args) throws CommandException {
        return completeModifier(args, placementModifier);
    }

    /**
     * Complete schematic modifier
     *
     * @param args arguments to complete
     * @return list of possible values
     * @throws CommandException if the arguments are invalid
     */
    @Override
    public List<String> completeSchematicModifier(Arguments args) throws CommandException {
        return completeModifier(args, schematicModifier);
    }

    private <T extends Nameable> List<String> completeModifier(Arguments args, Map<T, List<ModifierProvider>> map) throws CommandException {
        if (args.size() == 1) {
            return TabCompleteUtil.complete(args.asString(0), map.keySet().stream().map(Nameable::name));
        }
        if (args.size() == 2) {
            return completeProvider(args, getProviders(args, map).second);
        }
        return getProvider(args, map).second.complete(args.subArguments().subArguments(), null);
    }

    // util

    private <T extends Nameable> Pair<T, ModifierProvider> getProvider(Arguments args, Map<T, List<ModifierProvider>> map) throws CommandException {
        var provider = getProviders(args, map);
        var settingProvider = getSettingProvider(args.subArguments(), provider.second);
        return Pair.of(provider.first, settingProvider);
    }

    private <T extends Nameable> Pair<T, List<ModifierProvider>> getProviders(Arguments args, Map<T, List<ModifierProvider>> map) throws CommandException {
        return map.entrySet()
                .stream()
                .filter(e -> e.getKey().name().equals(args.asString(0)))
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .findFirst()
                .orElseThrow(() -> CommandException.message("Unkown modifier type"));
    }

    private <T extends SettingProvider<?>> T getSettingProvider(Arguments args, List<T> provider) throws CommandException {
        return provider.stream()
                .filter(p -> p.isMatch(args))
                .findFirst()
                .orElseThrow(() -> CommandException.message("Unkown modifier"));
    }

    private <T extends SettingProvider<?>> List<String> completeProvider(Arguments args, List<T> provider) {
        return TabCompleteUtil.complete(args.asString(0), provider.stream().map(p -> p.name()));
    }
}
