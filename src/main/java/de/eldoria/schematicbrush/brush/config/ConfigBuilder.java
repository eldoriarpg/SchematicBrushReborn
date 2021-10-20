package de.eldoria.schematicbrush.brush.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigBuilder {
    private final List<SchematicSet.SchematicSetBuilder> schematicSets = new ArrayList<>();
    private final BrushSettingsRegistry settingsRegistry;
    private final BrushSettings.BrushSettingsBuilder brushSettingsBuilder;

    public ConfigBuilder(BrushSettingsRegistry settingsRegistry) {
        this.settingsRegistry = settingsRegistry;
        brushSettingsBuilder = BrushSettings.newBrushSettingsBuilder();
        for (var entry : settingsRegistry.defaultPlacementModifier().entrySet()) {
            brushSettingsBuilder.setModifier(entry.getKey(), entry.getValue());
        }
    }

    public Optional<SchematicSet.SchematicSetBuilder> getSchematicSet(int id) {
        if (id >= schematicSets.size()) return Optional.empty();
        return Optional.ofNullable(schematicSets.get(id));
    }

    public boolean removeSchematicSet(int id) {
        if (id >= schematicSets.size()) return false;
        schematicSets.remove(id);
        return true;
    }

    public SchematicSet.SchematicSetBuilder createSchematicSet() {
        var builder = new SchematicSet.SchematicSetBuilder(settingsRegistry.defaultSelector());
        for (var entry : settingsRegistry.defaultSchematicModifier().entrySet()) {
            builder.withMutator(entry.getKey(), entry.getValue());
        }
        schematicSets.add(builder);
        return builder;
    }

    public void setPlacementModifier(PlacementModifier type, Mutator provider) {
        brushSettingsBuilder.setModifier(type, provider);
    }

    public BrushSettings build() {
        for (var schematicSet : schematicSets) {
            brushSettingsBuilder.addBrush(schematicSet.build());
        }
        return brushSettingsBuilder.build();
    }
}
