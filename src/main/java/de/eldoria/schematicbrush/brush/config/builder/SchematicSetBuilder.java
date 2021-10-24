package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This class is a builder to build a {@link SchematicSet}.
 */
public class SchematicSetBuilder {
    Selector selector;
    Map<SchematicModifier, Mutator<?>> schematicModifier;
    private Set<Schematic> schematics = Collections.emptySet();
    private int weight = -1;

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
    public SchematicSetBuilder withMutator(SchematicModifier type, Mutator mutation) {
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

    public Map<SchematicModifier, Mutator<?>> schematicModifier() {
        return schematicModifier;
    }

    public Set<Schematic> schematics() {
        return schematics;
    }

    public int weight() {
        return weight;
    }
}
