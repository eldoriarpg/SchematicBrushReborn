package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;
import de.eldoria.schematicbrush.util.Randomable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A brush configuration represents the settings of a single brush. A brush consists of one or more brushes represented
 * by a {@link SchematicSet} object. If more than one {@link SchematicSet} is present, a random {@link SchematicSet}
 * will be returned via the {@link #getRandomBrushConfig()} based on the {@link SchematicSet#weight()} of the
 * brushes. The brush settings contains some general brush settings, which apply to the whole brush and not only to
 * specific sub brushes.
 */
public final class BrushSettings implements Randomable {
    /**
     * List of all sub brushes this brush has.
     */
    private final List<SchematicSet> schematicSets;

    private final Map<PlacementModifier, SchematicMutator<?>> placementModifier;
    /**
     * The total weight of all brushes in the {@link #schematicSets} list
     */
    private final int totalWeight;

    private BrushSettings(List<SchematicSet> schematicSets, Map<PlacementModifier, SchematicMutator<?>> placementModifier) {
        this.schematicSets = schematicSets;
        this.placementModifier = placementModifier;

        // Count all weights, which have a weight set.
        var totalWeight = schematicSets.stream().filter(b -> b.weight() > 0).mapToInt(SchematicSet::weight).sum();
        // Count all weighted brushes
        var weighted = (int) schematicSets.stream().filter(b -> b.weight() > 0).count();
        // Count all unweighted weight
        var unweighted = (int) schematicSets.stream().filter(b -> b.weight() < 0).count();
        int defaultWeight;
        // Handle case, when no brush is weighted
        if (weighted == 0) {
            defaultWeight = 1;
        } else {
            // Calculate the default weight which is the average of all weightes brushes
            defaultWeight = totalWeight / weighted;
        }

        // Set the weight of all unweighted brushes
        schematicSets.stream().filter(b -> b.weight() < 0).forEach(b -> b.updateWeight(defaultWeight));

        // Calculate the total weight of all brushes
        this.totalWeight = schematicSets.stream().mapToInt(SchematicSet::weight).sum();
    }

    /**
     * Get a new builder for a brush configuration.
     *
     * @param brush brush which should be added
     * @return a brush builder with one brush added
     */
    public static BrushSettingsBuilder newSingleBrushSettingsBuilder(SchematicSet brush) {
        return new BrushSettingsBuilder(brush);
    }

    /**
     * Get a new builder for a brush configuration.
     *
     * @return a brush builder without any configuration
     */
    public static BrushSettingsBuilder newBrushSettingsBuilder() {
        return new BrushSettingsBuilder();
    }

    /**
     * Get a random brush from the {@link #schematicSets} list based on their {@link SchematicSet#weight()}.
     *
     * @return a random brush
     */
    public SchematicSet getRandomBrushConfig() {
        var random = randomInt(totalWeight);

        var count = 0;
        for (var brush : schematicSets) {
            if (count + brush.weight() > random) {
                return brush;
            }
            count += brush.weight();
        }
        return schematicSets.get(schematicSets.size() - 1);
    }

    /**
     * Counts all schematics in all brushes. No deduplication.
     *
     * @return total number of schematics in all brushes.
     */
    public int getSchematicCount() {
        return schematicSets.stream().map(b -> b.schematics().size()).mapToInt(Integer::intValue).sum();
    }

    /**
     * Get the brush configuration with a new brush combined. The options from the current brush are used.
     *
     * @param brush Brush to combine. Only the {@link SchematicSet} list is updated.
     * @return new brush configuration.
     */
    public BrushSettings combine(BrushSettings brush) {
        List<SchematicSet> brushes = new ArrayList<>(this.schematicSets);
        brushes.addAll(brush.schematicSets);
        return new BrushSettings(brushes, placementModifier);
    }

    public List<SchematicSet> schematicSets() {
        return schematicSets;
    }

    public int totalWeight() {
        return totalWeight;
    }

    public SchematicMutator<?> getMutator(PlacementModifier type) {
        return placementModifier.get(type);
    }

    public void mutate(PasteMutation mutation){
        placementModifier.values().forEach(m -> m.invoke(mutation));
    }

    public static final class BrushSettingsBuilder {
        /**
         * List of all sub brushes this brush has.
         */
        private final List<SchematicSet> brushes;
        private final Map<PlacementModifier, SchematicMutator<?>> placementModifier = new HashMap<>();

        private BrushSettingsBuilder(SchematicSet config) {
            brushes = Collections.singletonList(config);
        }

        private BrushSettingsBuilder() {
            brushes = new ArrayList<>();
        }


        /**
         * Add a brush to the brush configuration.
         *
         * @param brush brush which should be added
         * @return builder instance with brush added
         */
        public BrushSettingsBuilder addBrush(SchematicSet brush) {
            brushes.add(brush);
            return this;
        }

        /**
         * Build the brush configuration.
         *
         * @return A immutable brush config.
         */
        public BrushSettings build() {
            return new BrushSettings(brushes, placementModifier);
        }

        public void setModifier(PlacementModifier type, SchematicMutator<?> mutator) {
            placementModifier.put(type, mutator);
        }
    }
}
