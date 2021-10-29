package de.eldoria.schematicbrush.brush.config;

import de.eldoria.eldoutilities.commands.command.util.Arguments;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.schematicbrush.brush.config.flip.FlipProvider;
import de.eldoria.schematicbrush.brush.config.includeair.IncludeAirProvider;
import de.eldoria.schematicbrush.brush.config.offset.OffsetProvider;
import de.eldoria.schematicbrush.brush.config.placement.PlacementProvider;
import de.eldoria.schematicbrush.brush.config.replaceall.ReplaceAllProvider;
import de.eldoria.schematicbrush.brush.config.rotation.RotationProvider;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.config.selector.SelectorProvider;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.impl.SchematicBrushCache;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrushSettingsRegistry {
    private final List<SelectorProvider> selector = new ArrayList<>();
    private final Map<SchematicModifier, List<ModifierProvider>> schematicModifier = new LinkedHashMap<>();
    private final Map<PlacementModifier, List<ModifierProvider>> placementModifier = new LinkedHashMap<>();

    public void registerSelector(SelectorProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        selector.add(provider);
    }

    public void registerSchematicModifier(SchematicModifier type, ModifierProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        schematicModifier.computeIfAbsent(type, key -> new ArrayList<>()).add(provider);
    }

    public void registerPlacementModifier(PlacementModifier type, ModifierProvider provider) {
        ConfigurationSerialization.registerClass(provider.serializationClass());
        placementModifier.computeIfAbsent(type, key -> new ArrayList<>()).add(provider);
    }

    public void registerDefault(SchematicRegistry cache) {
        // SELECTORS
        registerSelector(SelectorProvider.NAME.apply(cache));
        registerSelector(SelectorProvider.REGEX.apply(cache));
        registerSelector(SelectorProvider.DIRECTORY.apply(cache));

        // SCHEMATIC MODIFIER
        registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.FIXED);
        registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.LIST);
        registerSchematicModifier(SchematicModifier.FLIP, FlipProvider.RANDOM);

        registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.FIXED);
        registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.LIST);
        registerSchematicModifier(SchematicModifier.ROTATION, RotationProvider.RANDOM);

        // PLACEMENT MODIFIER
        registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.FIXED);
        registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.LIST);
        registerPlacementModifier(PlacementModifier.OFFSET, OffsetProvider.RANGE);

        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.BOTTOM);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.DROP);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.MIDDLE);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.ORIGINAL);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.RAISE);
        registerPlacementModifier(PlacementModifier.PLACEMENT, PlacementProvider.TOP);

        registerPlacementModifier(PlacementModifier.INCLUDE_AIR, IncludeAirProvider.FIXED);

        registerPlacementModifier(PlacementModifier.REPLACE_ALL, ReplaceAllProvider.FIXED);
    }

    public Selector defaultSelector() {
        return selector.get(0).defaultSetting();
    }

    public Map<SchematicModifier, Mutator> defaultSchematicModifier() {
        return getDefaultMap(schematicModifier);
    }

    public Map<PlacementModifier, Mutator> defaultPlacementModifier() {
        return getDefaultMap(placementModifier);
    }

    private <T> Map<T, Mutator> getDefaultMap(Map<T, List<ModifierProvider>> map) {
        return map
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        (e) -> e.getValue().get(0).defaultSetting()));
    }

    // Parsing

    public Selector parseSelector(Arguments args) throws CommandException {
        return getSettingProvider(args, selector).parse(args.subArguments());
    }

    public Pair<SchematicModifier, Mutator> parseSchematicModifier(Arguments args) throws CommandException {
        var provider = getProvider(args, schematicModifier);
        return Pair.of(provider.first, provider.second.parse(args.subArguments().subArguments()));
    }

    public Pair<PlacementModifier, Mutator> parsePlacementModifier(Arguments args) throws CommandException {
        var provider = getProvider(args, placementModifier);
        return Pair.of(provider.first, provider.second.parse(args.subArguments().subArguments()));
    }

    public List<SelectorProvider> selector() {
        return Collections.unmodifiableList(selector);
    }

    public Map<SchematicModifier, List<ModifierProvider>> schematicModifier() {
        return Collections.unmodifiableMap(schematicModifier);
    }

    public Map<PlacementModifier, List<ModifierProvider>> placementModifier() {
        return Collections.unmodifiableMap(placementModifier);
    }

    // Tab completion

    public List<String> completeSelector(Arguments args, Player player) throws CommandException {
        if (args.size() == 1) {
            return TabCompleteUtil.complete(args.asString(0), selector.stream().map(SettingProvider::name));
        }
        return getSettingProvider(args, selector).complete(args.subArguments(), player);
    }

    public List<String> completePlacementModifier(Arguments args) throws CommandException {
        return completeModifier(args, placementModifier);
    }

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

    private <T extends SettingProvider<?>> List<String> completeProvider(Arguments args, List<T> provider) throws CommandException {
        return TabCompleteUtil.complete(args.asString(0), provider.stream().map(p -> p.name()));
    }
}
