package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.PlacementModifier;
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
    private final BrushSettingsRegistry settingsRegistry;
    private final Map<PlacementModifier, Mutator<?>> placementModifier = new HashMap<>();

    public BrushBuilder(BrushSettingsRegistry settingsRegistry) {
        this.settingsRegistry = settingsRegistry;
        for (var entry : settingsRegistry.defaultPlacementModifier().entrySet()) {
            setPlacementModifier(entry.getKey(), entry.getValue());
        }
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

    public SchematicSetBuilder createSchematicSet() {
        var builder = new SchematicSetBuilder(settingsRegistry.defaultSelector());
        for (var entry : settingsRegistry.defaultSchematicModifier().entrySet()) {
            builder.withMutator(entry.getKey(), entry.getValue());
        }
        schematicSets.add(builder);
        return builder;
    }

    public void setPlacementModifier(PlacementModifier type, Mutator provider) {
        placementModifier.put(type, provider);
    }

    public SchematicBrush build(Plugin plugin, Player owner) {
        var sets = schematicSets.stream().map(SchematicSetBuilder::build).collect(Collectors.toList());
        var settings = new BrushSettings(sets, placementModifier);
        return new SchematicBrush(plugin, owner, settings);
    }

    public static BrushBuilder fromBrush(SchematicBrush brush, BrushSettingsRegistry registry) {
        return brush.toBuilder(registry);
    }

    public void addSchematicSet(SchematicSetBuilder schematicSetBuilder) {
        schematicSets.add(schematicSetBuilder);
    }

    public List<SchematicSetBuilder> schematicSets() {
        return Collections.unmodifiableList(schematicSets);
    }

    public Map<PlacementModifier, Mutator<?>> placementModifier() {
        return Collections.unmodifiableMap(placementModifier);
    }
}
