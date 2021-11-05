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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.brush.config.builder.BuildUtil.buildModifier;

/**
 * This class is a builder to build a {@link SchematicSetImpl}.
 */
public class SchematicSetBuilderImpl implements SchematicSetBuilder {
    private Selector selector;
    private Map<Nameable, Mutator<?>> schematicModifier = new HashMap<>();
    private Set<Schematic> schematics = Collections.emptySet();
    private int weight = -1;

    public SchematicSetBuilderImpl(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        selector = map.getValue("selector");
        schematicModifier = map.getMap("modifiers", (key, v) -> Nameable.of(key));
        weight = map.getValue("weight");
    }

    public SchematicSetBuilderImpl(Selector selector) {
        this.selector = selector;
    }

    private SchematicSetBuilderImpl(Selector selector, Map<Nameable, Mutator<?>> schematicModifier, Set<Schematic> schematics, int weight) {
        this.selector = selector;
        this.schematicModifier = schematicModifier;
        this.schematics = schematics;
        this.weight = weight;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
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
     * @param registry registry
     * @param id       id
     * @return component
     */
    @Override
    public String interactComponent(BrushSettingsRegistry registry, int id) {
        var selector = String.format("<%s>Selector: <%s>", Colors.HEADING, Colors.CHANGE);
        selector += registry.selector().stream()
                .map(sel -> String.format("<click:%s:'/sbr modifyset %s selector %s '>[%s]</click>", sel.commandType(), id, sel.name(), sel.name()))
                .collect(Collectors.joining(", "));
        selector += String.format("%n  <hover:show_text:'%s'>%s</hover>", schematicInfo(), BuildUtil.renderProvider(selector()));

        var mutatorMap = schematicModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : registry.schematicModifier().entrySet()) {
            modifierStrings.add(buildModifier("/sbr modifyset " + id, entry.getKey(), entry.getValue(), mutatorMap.get(entry.getKey())));
        }
        var modifier = String.join("\n", modifierStrings);
        var weight = String.format("<%s>Weight: <%s>%s <click:suggest_command:'/sbr modifyset %s weight '><%s>[change]</click>",
                Colors.HEADING, Colors.VALUE, weight(), id, Colors.CHANGE);
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

        result += schematics.stream()
                .limit(10)
                .map(schem -> String.format("<%s>%s", Colors.VALUE, schem.name()))
                .collect(Collectors.joining("\n"));

        if (schematicCount() > 10) {
            result += String.format("%n<%s>... and %s more.", Colors.NEUTRAL, schematics.size() - 10);
        }
        return result;
    }

    @Override
    public SchematicSetBuilderImpl clone() {
        Map<Nameable, Mutator<?>> mutatorCopy = schematicModifier.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new SchematicSetBuilderImpl(selector, mutatorCopy, new LinkedHashSet<>(schematics), weight);
    }
}
