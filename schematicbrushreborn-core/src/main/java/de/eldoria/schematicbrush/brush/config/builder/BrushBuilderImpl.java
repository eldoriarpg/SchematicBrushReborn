/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.SchematicBrushImpl;
import de.eldoria.schematicbrush.brush.config.BrushSettingsImpl;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
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

public final class BrushBuilderImpl implements BrushBuilder {
    private final List<SchematicSetBuilder> schematicSets;
    private final Player owner;
    private final BrushSettingsRegistry settingsRegistry;
    private final SchematicRegistry schematicRegistry;
    private final Map<PlacementModifier, Mutator<?>> placementModifier;

    BrushBuilderImpl(List<SchematicSetBuilder> schematicSets, Player owner, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry, Map<PlacementModifier, Mutator<?>> placementModifier) {
        this.schematicSets = schematicSets;
        this.owner = owner;
        this.settingsRegistry = settingsRegistry;
        this.schematicRegistry = schematicRegistry;
        this.placementModifier = placementModifier;
    }

    public BrushBuilderImpl(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        owner = player;
        schematicSets = new ArrayList<>();
        placementModifier = new HashMap<>();
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
    @Override
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
    @Override
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
    @Override
    public int createSchematicSet() {
        var builder = new SchematicSetBuilderImpl(settingsRegistry.defaultSelector());
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
    @Override
    public <T extends PlacementModifier> void setPlacementModifier(T type, Mutator<?> provider) {
        placementModifier.put(type, provider);
    }

    @Override
    public <T extends PlacementModifier> void removePlacementModifier(T type) {
        placementModifier.remove(type);
    }

    /**
     * Build the schematic brush
     *
     * @param plugin plugin
     * @param owner  brush owner
     * @return new schematic brush instance
     */
    @Override
    public SchematicBrush build(Plugin plugin, Player owner) {
        var sets = schematicSets.stream().map(SchematicSetBuilder::build).collect(Collectors.toList());
        var settings = new BrushSettingsImpl(sets, placementModifier);
        return new SchematicBrushImpl(plugin, owner, settings);
    }

    /**
     * Add a new schematic set builder
     *
     * @param schematicSetBuilder builder to add
     */
    @Override
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
    @Override
    public List<SchematicSetBuilder> schematicSets() {
        return Collections.unmodifiableList(schematicSets);
    }

    /**
     * Get the current placement modifier
     *
     * @return unmodifiable map of the placement modifier
     */
    @Override
    public Map<? extends PlacementModifier, Mutator<?>> placementModifier() {
        return Collections.unmodifiableMap(placementModifier);
    }

    /**
     * Reset all modifier to default and clear schematic sets.
     */
    @Override
    public void clear() {
        placementModifier.clear();
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
    @Override
    public int getSchematicCount() {
        return schematicSets.stream().mapToInt(SchematicSetBuilder::schematicCount).sum();
    }

    /**
     * Reload all schematics in the brush
     */
    @Override
    public void refresh() {
        for (var schematicSet : schematicSets) {
            schematicSet.refreshSchematics(owner, schematicRegistry);
        }
    }

    @Override
    public BrushBuilderSnapshot snapshot() {
        Map<PlacementModifier, Mutator<?>> placementModifier = this.placementModifier.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, key -> key.getValue().copy()));
        var schematicSets = this.schematicSets.stream()
                .map(SchematicSetBuilder::copy)
                .collect(Collectors.toCollection(ArrayList::new));
        return new BrushBuilderSnapshotImpl(placementModifier, schematicSets);
    }

}
