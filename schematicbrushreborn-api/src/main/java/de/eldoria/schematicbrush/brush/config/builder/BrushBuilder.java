package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class BrushBuilder {
    private final List<SchematicSetBuilder> schematicSets = new ArrayList<>();
    private final Player owner;
    private final BrushSettingsRegistry settingsRegistry;
    private final SchematicRegistry schematicRegistry;
    private final Map<PlacementModifier, Mutator<?>> placementModifier = new HashMap<>();

    public BrushBuilder(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        owner = player;
        this.settingsRegistry = settingsRegistry;
        this.schematicRegistry = schematicRegistry;
        for (var entry : settingsRegistry.defaultPlacementModifier().entrySet()) {
            setPlacementModifier(entry.getKey(), entry.getValue());
        }
    }

    public static BrushBuilder fromBrush(SchematicBrush brush, BrushSettingsRegistry registry, SchematicRegistry schematicRegistry) {
        return brush.toBuilder(registry, schematicRegistry);
    }

    public Optional<SchematicSetBuilder> getSchematicSet(int id) {
        if (id >= schematicSets.size()) return Optional.empty();
        return Optional.ofNullable(schematicSets.get(id));
    }

    public boolean removeSchematicSet(int id) {
        if (id >= schematicSets.size()) return false;
        schematicSets.remove(id);
        return true;
    }

    public int createSchematicSet() {
        var builder = new SchematicSetBuilder(settingsRegistry.defaultSelector());
        builder.enforceDefaultModifier(settingsRegistry);
        schematicSets.add(builder);
        builder.refreshSchematics(owner, schematicRegistry);
        return schematicSets.size() - 1;
    }

    public void setPlacementModifier(PlacementModifier type, Mutator<?> provider) {
        placementModifier.put(type, provider);
    }

    public SchematicBrush build(Plugin plugin, Player owner) {
        var sets = schematicSets.stream().map(SchematicSetBuilder::build).collect(Collectors.toList());
        var settings = new BrushSettings(sets, placementModifier);
        return new SchematicBrush(plugin, owner, settings);
    }

    public void addSchematicSet(SchematicSetBuilder schematicSetBuilder) {
        schematicSetBuilder.refreshSchematics(owner, schematicRegistry);
        schematicSetBuilder.enforceDefaultModifier(settingsRegistry);
        schematicSets.add(schematicSetBuilder);
    }

    public List<SchematicSetBuilder> schematicSets() {
        return Collections.unmodifiableList(schematicSets);
    }

    public Map<PlacementModifier, Mutator<?>> placementModifier() {
        return Collections.unmodifiableMap(placementModifier);
    }

    public void clear() {
        for (var entry : settingsRegistry.defaultPlacementModifier().entrySet()) {
            setPlacementModifier(entry.getKey(), entry.getValue());
        }
        schematicSets.clear();
    }

    public int getSchematicCount() {
        return schematicSets.stream().mapToInt(SchematicSetBuilder::schematicCount).sum();
    }

    // TODO add method and command to reload all current selectors
}
