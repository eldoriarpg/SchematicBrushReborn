package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
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

    /**
     * Get the schematic set builder for the set with the id
     *
     * @param id id
     * @return set if the id exists
     */
    public Optional<SchematicSetBuilder> getSchematicSet(int id) {
        if (id >= schematicSets.size()) return Optional.empty();
        return Optional.ofNullable(schematicSets.get(id));
    }

    /**
     * Remove the set with the id
     *
     * @param id id
     * @return true if the set with the id was removed
     */
    public boolean removeSchematicSet(int id) {
        if (id >= schematicSets.size()) return false;
        schematicSets.remove(id);
        return true;
    }

    /**
     * Create a new schematic set
     *
     * @return id of the created schematic set
     */
    public int createSchematicSet() {
        var builder = new SchematicSetBuilder(settingsRegistry.defaultSelector());
        builder.enforceDefaultModifier(settingsRegistry);
        schematicSets.add(builder);
        builder.refreshSchematics(owner, schematicRegistry);
        return schematicSets.size() - 1;
    }

    /**
     * Sets the placement modifier
     *
     * @param type     type
     * @param provider provider
     */
    public void setPlacementModifier(PlacementModifier type, Mutator<?> provider) {
        placementModifier.put(type, provider);
    }

    /**
     * Build the schematic brush
     *
     * @param plugin plugin
     * @param owner  brush owner
     * @return new schematic brush instance
     */
    public SchematicBrush build(Plugin plugin, Player owner) {
        var sets = schematicSets.stream().map(SchematicSetBuilder::build).collect(Collectors.toList());
        var settings = new BrushSettings(sets, placementModifier);
        return new SchematicBrush(plugin, owner, settings);
    }

    /**
     * Add a new schematic set builder
     *
     * @param schematicSetBuilder builder to add
     */
    public void addSchematicSet(SchematicSetBuilder schematicSetBuilder) {
        schematicSetBuilder.refreshSchematics(owner, schematicRegistry);
        schematicSetBuilder.enforceDefaultModifier(settingsRegistry);
        schematicSets.add(schematicSetBuilder);
    }

    /**
     * Get the registered schematic set builder
     *
     * @return unmodifiable list of schematic sets
     */
    public List<SchematicSetBuilder> schematicSets() {
        return Collections.unmodifiableList(schematicSets);
    }

    /**
     * Get the current placement modifier
     *
     * @return unmodifiable map of the placement modifier
     */
    public Map<PlacementModifier, Mutator<?>> placementModifier() {
        return Collections.unmodifiableMap(placementModifier);
    }

    /**
     * Reset all modifier to default and clear schematic sets.
     */
    public void clear() {
        for (var entry : settingsRegistry.defaultPlacementModifier().entrySet()) {
            setPlacementModifier(entry.getKey(), entry.getValue());
        }
        schematicSets.clear();
    }

    /**
     * Get the schematic count of the brush
     *
     * @return schematic count
     */
    public int getSchematicCount() {
        return schematicSets.stream().mapToInt(SchematicSetBuilder::schematicCount).sum();
    }

    // TODO add method and command to reload all current selectors

    /**
     * Reload all schematics in the brush
     */
    public void refresh() {
        for (var schematicSet : schematicSets) {
            schematicSet.refreshSchematics(owner, schematicRegistry);
        }
    }

}
