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
import de.eldoria.schematicbrush.brush.config.provider.*;
import de.eldoria.schematicbrush.brush.config.schematics.SchematicSelection;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.brush.exceptions.AlreadyRegisteredException;
import de.eldoria.schematicbrush.brush.provider.*;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry to register brush settings
 */
public class BrushSettingsRegistryImpl implements BrushSettingsRegistry {
    private final List<SelectorProvider> selector = new ArrayList<>();
    private final List<SchematicSelectionProvider> schematicSelection = new ArrayList<>();
    private final Map<SchematicModifier, List<ModifierProvider>> schematicModifier = new LinkedHashMap<>();
    private final Map<PlacementModifier, List<ModifierProvider>> placementModifier = new LinkedHashMap<>();

    @Override
    public void registerSelector(SelectorProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        if (selector.contains(provider)) {
            throw new AlreadyRegisteredException(provider);
        }
        selector.add(provider);
    }

    @Override
    public void registerSchematicSelection(SchematicSelectionProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        if (schematicSelection.contains(provider)) {
            throw new AlreadyRegisteredException(provider);
        }
        schematicSelection.add(provider);
    }

    @Override
    public void registerSchematicModifier(SchematicModifier type, ModifierProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        if (schematicModifier.containsKey(type) && schematicModifier.get(type).contains(provider)) {
            throw new AlreadyRegisteredException(type, provider);
        }
        schematicModifier.computeIfAbsent(type, key -> new ArrayList<>()).add(provider);
    }

    @Override
    public void registerPlacementModifier(PlacementModifier type, ModifierProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        if (placementModifier.containsKey(type) && placementModifier.get(type).contains(provider)) {
            throw new AlreadyRegisteredException(type, provider);
        }
        placementModifier.computeIfAbsent(type, key -> new ArrayList<>()).add(provider);
    }

    @Override
    public Selector defaultSelector() {
        return selector.get(0).defaultSetting();
    }

    @Override
    public Map<SchematicModifier, Mutator<?>> defaultSchematicModifier() {
        return getDefaultMap(schematicModifier);
    }

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

    @Override
    public Selector parseSelector(Arguments args) throws CommandException {
        var provider = getSettingProvider(args, selector);
        CommandAssertions.invalidArguments(args.subArguments(), provider.arguments());
        return provider.parse(args.subArguments());
    }

    @Override
    public SchematicSelection parseSchematicSelection(Arguments args) throws CommandException {
        var provider = getSettingProvider(args, schematicSelection);
        CommandAssertions.invalidArguments(args.subArguments(), provider.arguments());
        return provider.parse(args.subArguments());
    }

    @Override
    public Pair<SchematicModifier, Mutator<?>> parseSchematicModifier(Arguments args) throws CommandException {
        var provider = getProvider(args, schematicModifier);
        var subArguments = args.subArguments().subArguments();
        CommandAssertions.invalidArguments(subArguments, provider.second.arguments());
        return Pair.of(provider.first, provider.second.parse(subArguments));
    }

    @Override
    public Pair<PlacementModifier, Mutator<?>> parsePlacementModifier(Arguments args) throws CommandException {
        var provider = getProvider(args, placementModifier);
        var subArguments = args.subArguments().subArguments();
        CommandAssertions.invalidArguments(subArguments, provider.second.arguments());
        return Pair.of(provider.first, provider.second.parse(subArguments));
    }

    @Override
    public List<SelectorProvider> selector() {
        return Collections.unmodifiableList(selector);
    }

    @Override
    public List<SchematicSelectionProvider> schematicSelections() {
        return Collections.unmodifiableList(schematicSelection);
    }

    @Override
    public Map<SchematicModifier, List<ModifierProvider>> schematicModifier() {
        return Collections.unmodifiableMap(schematicModifier);
    }

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

    @Override
    public List<String> completeSelector(Arguments args, Player player) throws CommandException {
        if (args.size() == 1) {
            return TabCompleteUtil.complete(args.asString(0), selector.stream().map(SettingProvider::name));
        }
        return getSettingProvider(args, selector).complete(args.subArguments(), player);
    }

    @Override
    public List<String> completeSchematicSelection(Arguments args, Player player) throws CommandException {
        if (args.size() == 1) {
            return TabCompleteUtil.complete(args.asString(0), schematicSelection.stream().map(SettingProvider::name));
        }
        return getSettingProvider(args, schematicSelection).complete(args.subArguments(), player);
    }

    @Override
    public List<String> completePlacementModifier(Arguments args) throws CommandException {
        return completeModifier(args, placementModifier);
    }

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

    public void registerDefaults(SchematicRegistry schematics) {
        // SELECTORS
        registerSelector(SelectorProviderImpl.NAME.apply(schematics));
        registerSelector(SelectorProviderImpl.REGEX.apply(schematics));
        registerSelector(SelectorProviderImpl.DIRECTORY.apply(schematics));

        // SCHEMATIC MODIFIER
        registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.FIXED);
        registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.LIST);
        registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.RANDOM);

        registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.FIXED);
        registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.LIST);
        registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.RANDOM);

        registerSchematicModifier(SchematicModifier.OFFSET, OffsetProvider.FIXED);
        registerSchematicModifier(SchematicModifier.OFFSET, OffsetProvider.LIST);
        registerSchematicModifier(SchematicModifier.OFFSET, OffsetProvider.RANGE);

        // PLACEMENT MODIFIER
        registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.FIXED);
        registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.LIST);
        registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.RANGE);

        registerPlacementModifier(PlacementModifier.FLIP, FlipProvider.FIXED);
        registerPlacementModifier(PlacementModifier.FLIP, FlipProvider.LIST);
        registerPlacementModifier(PlacementModifier.FLIP, FlipProvider.RANDOM);

        registerPlacementModifier(PlacementModifier.ROTATION, RotationProvider.FIXED);
        registerPlacementModifier(PlacementModifier.ROTATION, RotationProvider.LIST);
        registerPlacementModifier(PlacementModifier.ROTATION, RotationProvider.RANDOM);

        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.BOTTOM);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.DROP);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.MIDDLE);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.ORIGINAL);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.RAISE);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.TOP);

        registerPlacementModifier(PlacementModifier.INCLUDE_AIR, IncludeAirProvider.FIXED);

        registerPlacementModifier(PlacementModifier.REPLACE_ALL, ReplaceAllProvider.FIXED);

        registerPlacementModifier(PlacementModifier.FILTER, FilterProvider.BLOCK_FILTER);

        // Schematic selection
        registerSchematicSelection(SchematicSelectionProviderImpl.RANDOM_SELECTION);
        registerSchematicSelection(SchematicSelectionProviderImpl.ORDERED_SELECTION);
        registerSchematicSelection(SchematicSelectionProviderImpl.LOCKED_RANDOM_SELECTION);
        registerSchematicSelection(SchematicSelectionProviderImpl.LOCKED_ORDERED_SELECTION);
    }
}
