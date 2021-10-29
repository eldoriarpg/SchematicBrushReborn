package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.Nameable;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.SettingProvider;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.brush.config.builder.BuildUtil.buildModifier;

/**
 * This class is a builder to build a {@link SchematicSet}.
 */
public class SchematicSetBuilder implements ConfigurationSerializable {
    private Selector selector;
    private Map<Nameable, Mutator<?>> schematicModifier = new HashMap<>();
    private Set<Schematic> schematics = Collections.emptySet();
    private int weight = -1;

    public SchematicSetBuilder(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        selector = map.getValue("selector");
        schematicModifier = map.getMap("modifiers", (k, v) -> Nameable.of(k));
        weight = map.getValue("weight");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("selector", selector)
                .addMap("schematicModifier", schematicModifier, (k, v) -> k.name())
                .add("weight", weight)
                .build();
    }

    public SchematicSetBuilder(Selector selector) {
        this.selector = selector;
    }

    /**
     * Set the schematic list of brush.
     *
     * @param schematics schematics to set
     * @return instance with schematics set
     */
    public SchematicSetBuilder withSchematics(Set<Schematic> schematics) {
        this.schematics = schematics;
        return this;
    }

    /**
     * Set the rotation of the brush.
     *
     * @param mutation rotation of the brush
     * @return instance with rotation set.
     */
    public <T extends Nameable> SchematicSetBuilder withMutator(T type, Mutator<?> mutation) {
        schematicModifier.put(type, mutation);
        return this;
    }

    /**
     * Set the weight of the brush.
     *
     * @param weight weight of the brush
     * @return instance with weight set
     */
    public SchematicSetBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    /**
     * Build a sub brush.
     *
     * @return new sub brush instance with set values and default if not set.
     */
    public SchematicSet build() {
        return new SchematicSet(schematics, selector, schematicModifier, weight);
    }

    public Selector selector() {
        return selector;
    }

    public Map<? extends Nameable, Mutator<?>> schematicModifier() {
        return schematicModifier;
    }

    public Set<Schematic> schematics() {
        return schematics;
    }

    public int weight() {
        return weight;
    }

    public void selector(Selector selector) {
        this.selector = selector;
    }

    public void refreshSchematics(Player player, SchematicRegistry registry) {
        schematics = selector.select(player, registry);
    }

    public int schematicCount() {
        return schematics.size();
    }

    public String interactComponent(BrushSettingsRegistry registry, int id) {
        var selector = String.format("<%s>Selector: <%s>", Colors.HEADING, Colors.CHANGE);
        selector += registry.selector().stream()
                .map(SettingProvider::name)
                .map(sel -> String.format("<click:suggest_command:'/sbr modifyset %s selector %s '>[%s]</click>", id, sel, sel))
                .collect(Collectors.joining(", "));
        selector += "\n" + BuildUtil.renderProvider(selector());

        var mutatorMap = schematicModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : registry.schematicModifier().entrySet()) {
            modifierStrings.add(buildModifier("/sbr modifyset " + id, entry.getKey(), entry.getValue(), mutatorMap.get(entry.getKey())));
        }
        var modifier = String.join("\n", modifierStrings);
        var weight = String.format("<%s>Weight: <%s>%s <click:suggest_command:'/sbr modifyset weight '><%s>[change]</click>", Colors.NAME, Colors.VALUE, weight(), Colors.CHANGE);
        return String.join("\n", selector, modifier, weight);
    }

    public String infoComponent() {
        var selector = BuildUtil.renderProvider(selector());

        var mutatorMap = schematicModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : mutatorMap.entrySet()) {
            modifierStrings.add(entry.getKey().name() + "\n  " + BuildUtil.renderProvider(entry.getValue()));
        }
        var modifier = String.join("\n", modifierStrings);
        var weight = String.format("<%s>Weight: <%s>%s ", Colors.NAME, Colors.VALUE, weight());
        return String.join("\n", selector, modifier, weight);
    }
}
