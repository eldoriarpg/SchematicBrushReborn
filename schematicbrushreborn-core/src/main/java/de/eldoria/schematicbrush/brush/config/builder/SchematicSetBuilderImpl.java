/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.SchematicSetImpl;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.brush.config.builder.BuildUtil.buildModifier;

/**
 * This class is a builder to build a {@link SchematicSetImpl}.
 */
@SerializableAs("sbrSchematicSetBuilder")
public class SchematicSetBuilderImpl implements SchematicSetBuilder {
    private static final String WEIGHT_DESCRIPTION = "The weight of the schematic set when multiple sets are used.\nHigher numbers will result in more schematics from this set.";
    private Selector selector;
    private Map<Nameable, Mutator<?>> schematicModifier = new HashMap<>();
    private Set<Schematic> schematics = Collections.emptySet();
    private int weight = -1;

    public SchematicSetBuilderImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        selector = map.getValue("selector");
        schematicModifier = map.getMap("modifiers", (key, v) -> Nameable.of(key));
        weight = map.getValue("weight");
        schematicModifier.entrySet().removeIf(entry -> entry.getValue() == null);
    }

    public SchematicSetBuilderImpl(Selector selector) {
        this.selector = selector;
    }

    private SchematicSetBuilderImpl(Selector selector, Map<Nameable, Mutator<?>> schematicModifier, Set<Schematic> schematics, int weight) {
        this.selector = selector;
        this.schematicModifier = schematicModifier;
        this.schematics = schematics;
        this.weight = weight;
        schematicModifier.entrySet().removeIf(entry -> entry.getValue() == null);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        schematicModifier.entrySet().removeIf(entry -> entry.getValue() == null);
        return SerializationUtil.newBuilder()
                .add("selector", selector)
                .addMap("modifiers", schematicModifier, (key, v) -> key.name())
                .add("weight", weight)
                .build();
    }

    /**
     * Set the rotation of the brush.
     *
     * @param <T>      type of mutator
     * @param type     type of mutator
     * @param mutation rotation of the brush
     */
    @Override
    public <T extends Nameable> void withMutator(T type, Mutator<?> mutation) {
        schematicModifier.put(type, mutation);
    }

    /**
     * Set the default modifier
     *
     * @param registry registry
     */
    @Override
    public void enforceDefaultModifier(BrushSettingsRegistry registry) {
        for (var entry : registry.defaultSchematicModifier().entrySet()) {
            schematicModifier.computeIfAbsent(entry.getKey(), key -> entry.getValue());
        }
    }

    /**
     * Set the weight of the brush.
     *
     * @param weight weight of the brush
     */
    @Override
    public void withWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Build a sub brush.
     *
     * @return new sub brush instance with set values and default if not set.
     */
    @Override
    public SchematicSet build() {
        return new SchematicSetImpl(schematics, selector, schematicModifier, weight);
    }

    /**
     * Selector
     *
     * @return selector
     */
    @Override
    public Selector selector() {
        return selector;
    }

    /**
     * Schematic modifier
     *
     * @return unmodifiable map
     */
    @Override
    public Map<? extends Nameable, Mutator<?>> schematicModifier() {
        return Collections.unmodifiableMap(schematicModifier);
    }

    /**
     * Schematics
     *
     * @return schematics
     */
    @Override
    public Set<Schematic> schematics() {
        return schematics;
    }

    /**
     * Weight
     *
     * @return weight
     */
    @Override
    public int weight() {
        return weight;
    }

    /**
     * Selector
     *
     * @param selector selector
     */
    @Override
    public void selector(Selector selector) {
        this.selector = selector;
    }

    /**
     * refresh all selected schematics
     *
     * @param player   player
     * @param registry registry
     */
    @Override
    public void refreshSchematics(Player player, SchematicRegistry registry) {
        schematics = selector.select(player, registry);
    }

    /**
     * Schematic count of set
     *
     * @return schematic count
     */
    @Override
    public int schematicCount() {
        return schematics.size();
    }

    /**
     * Schematic set as interactable component
     *
     * @param player   the player
     * @param registry registry
     * @param id       id
     * @return component
     */
    @Override
    public String interactComponent(Player player, BrushSettingsRegistry registry, int id) {
        var selector = String.format("<%s>Selector: <%s>", Colors.HEADING, Colors.CHANGE);
        selector += registry.selector().stream()
                .map(sel -> String.format("<click:%s:'/sbr modifyset %s selector %s '><hover:show_text:'<%s>%s'>[%s]</click>", sel.commandType(), id, sel.name(), Colors.NEUTRAL, sel.description(), sel.name()))
                .collect(Collectors.joining(" "));
        selector += String.format("%n  <hover:show_text:'%s'>%s</hover>", schematicInfo(), BuildUtil.renderProvider(selector()));

        var mutatorMap = schematicModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : registry.schematicModifier().entrySet()) {
            modifierStrings.add(buildModifier(player, "/sbr modifyset " + id, entry.getKey(), entry.getValue(), mutatorMap.get(entry.getKey())));
        }
        var modifier = String.join("\n", modifierStrings);
        var weight = String.format("<%s><hover:show_text:'<%s>%s'>Weight:</hover> <%s>%s <click:suggest_command:'/sbr modifyset %s weight '><%s>[change]</click>",
                Colors.HEADING, Colors.NEUTRAL, WEIGHT_DESCRIPTION, Colors.VALUE, weight(), id, Colors.CHANGE);
        return String.join("\n", selector, modifier, weight);
    }

    /**
     * Schematic set as component
     *
     * @return component
     */
    @Override
    public String infoComponent() {
        var selector = BuildUtil.renderProvider(selector());

        var mutatorMap = schematicModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : mutatorMap.entrySet()) {
            modifierStrings.add(String.format("<%s>%s%n  %s", Colors.HEADING, entry.getKey().name(), BuildUtil.renderProvider(entry.getValue())));
        }
        var modifier = String.join("\n", modifierStrings);
        var weight = String.format("<%s>Weight: <%s>%s ", Colors.HEADING, Colors.VALUE, weight());
        return String.join("\n", selector, modifier, weight);
    }

    private String schematicInfo() {
        var result = String.format("<%s>%s<%s> Schematics%n", Colors.NAME, schematics.size(), Colors.HEADING);

        var showSchematics = schematics.stream()
                .sorted()
                .map(Schematic::name)
                .toList();

        if (schematics.size() > 10) {
            List<String> schematics = new ArrayList<>(showSchematics.subList(0, 5));
            schematics.add("...");
            schematics.addAll(showSchematics.subList(showSchematics.size() - 5, showSchematics.size()));
            showSchematics = schematics;
        }
        result += showSchematics.stream()
                .map(schem -> String.format("<%s>%s", Colors.VALUE, schem))
                .collect(Collectors.joining("\n"));

        if (schematicCount() > 10) {
            result += String.format("%n<%s>... and %s more.", Colors.NEUTRAL, schematics.size() - 10);
        }
        return result;
    }

    @Override
    public SchematicSetBuilderImpl copy() {
        Map<Nameable, Mutator<?>> mutatorCopy = schematicModifier.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().copy()));
        return new SchematicSetBuilderImpl(selector, mutatorCopy, new LinkedHashSet<>(schematics), weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchematicSetBuilder builder)) return false;

        if (weight != builder.weight()) return false;
        if (!selector.equals(builder.selector())) return false;
        if (!schematicModifier.equals(builder.schematicModifier())) return false;
        return schematics.equals(builder.schematics());
    }

    @Override
    public int hashCode() {
        var result = selector.hashCode();
        result = 31 * result + schematicModifier.hashCode();
        result = 31 * result + schematics.hashCode();
        result = 31 * result + weight;
        return result;
    }
}
