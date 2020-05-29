package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.parameter.Placement;
import de.eldoria.schematicbrush.util.Randomable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A brush configuration represents the settings of a single brush.
 * A brush consists of one or more brushes represented by a {@link SchematicSet} object.
 * If more than one {@link SchematicSet} is present, a random {@link SchematicSet} will be returned via
 * the {@link #getRandomBrushConfig()} based on the {@link SchematicSet#getWeight()} of the brushes.
 * The brush settings contains some general brush settings,
 * which apply to the whole brush and not only to specific sub brushes.
 */
@Getter
public final class BrushSettings implements Randomable {
    /**
     * List of all sub brushes this brush has.
     */
    private final List<SchematicSet> schematicSets;
    /**
     * True if the air of the schematic should replace non air blocks
     */
    private final boolean includeAir;
    /**
     * True if the schematic should only be pasted where the block material is {@link org.bukkit.Material#AIR}
     */
    private final boolean replaceAll;
    /**
     * The y offset which will be applied before pasting to the position which was clicked by the user.
     */
    private final int yOffset;
    /**
     * Method which determins the origin of the schematic.
     */
    private final Placement placement;
    /**
     * The total weight of all brushes in the {@link #schematicSets} list
     */
    private final int totalWeight;

    private BrushSettings(List<SchematicSet> schematicSets, boolean includeAir, boolean replaceAll, int yOffset,
                          Placement placement) {
        this.schematicSets = schematicSets;
        this.includeAir = includeAir;
        this.replaceAll = replaceAll;
        this.yOffset = yOffset;
        this.placement = placement;

        // Count all weights, which have a weight set.
        int totalWeight = schematicSets.stream().filter(b -> b.getWeight() > 0).mapToInt(SchematicSet::getWeight).sum();
        // Count all weighted brushes
        int weighted = (int) schematicSets.stream().filter(b -> b.getWeight() > 0).count();
        // Count all unweighted brushes
        int unweighted = (int) schematicSets.stream().filter(b -> b.getWeight() < 0).count();
        int defaultWeight;
        // Handle case, when no brush is weighted
        if (weighted == 0) {
            defaultWeight = 1;
        } else {
            // Calculate the default weight which is the average of all weightes brushes
            defaultWeight = totalWeight / weighted;
        }

        // Set the weight of all unweighted brushes
        schematicSets.stream().filter(b -> b.getWeight() < 0).forEach(b -> b.updateWeight(defaultWeight));

        // Calculate the total weight of all brushes
        this.totalWeight = schematicSets.stream().mapToInt(SchematicSet::getWeight).sum();
    }

    /**
     * Get a random brush from the {@link #schematicSets} list based on their {@link SchematicSet#getWeight()}.
     *
     * @return a random brush
     */
    public SchematicSet getRandomBrushConfig() {
        int random = randomInt(totalWeight);

        int count = 0;
        for (SchematicSet brush : schematicSets) {
            if (count + brush.getWeight() > random) {
                return brush;
            }
            count += brush.getWeight();
        }
        return schematicSets.get(schematicSets.size() - 1);
    }

    /**
     * Counts all schematics in all brushes. No deduplication.
     *
     * @return total number of schematics in all brushes.
     */
    public int getSchematicCount() {
        return schematicSets.stream().map(b -> b.getSchematics().size()).mapToInt(Integer::intValue).sum();
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
     * Get the brush configuration with a new brush combined.
     * The options from the current brush are used.
     *
     * @param brush Brush to combine. Only the {@link SchematicSet} list is updated.
     * @return new brush configuration.
     */
    public BrushSettings combine(BrushSettings brush) {
        List<SchematicSet> brushes = new ArrayList<>(this.schematicSets);
        brushes.addAll(brush.schematicSets);
        return new BrushSettings(brushes, includeAir, replaceAll, yOffset, placement);
    }

    public static final class BrushSettingsBuilder {
        /**
         * List of all sub brushes this brush has.
         */
        private final List<SchematicSet> brushes;
        /**
         * True if the air of the schematic should replace non air blocks
         */
        private boolean includeAir = false;
        /**
         * False if the schematic should only be pasted where the block material is {@link org.bukkit.Material#AIR}
         */
        private boolean replaceAll = false;
        /**
         * The y offset which will be applied before pasting to the position which was clicked by the user.
         */
        private int yOffset = 0;
        /**
         * Method which determins the origin of the schematic.
         */
        private Placement placement = Placement.DROP;


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
         * Defines if the brush should include air when pasting. Default: {@code false}
         *
         * @param includeAir True if the air of the schematic should replace non air blocks
         * @return builder instance with changed state
         */
        public BrushSettingsBuilder includeAir(boolean includeAir) {
            this.includeAir = includeAir;
            return this;
        }

        /**
         * Defines if the brush should only be pasted where the block is air. Default: {@code false}
         *
         * @param replaceAirOnly True if the schematic should only be pasted
         *                       where the block material is {@link org.bukkit.Material#AIR}
         * @return builder instance with changed state
         */
        public BrushSettingsBuilder replaceAll(boolean replaceAirOnly) {
            this.replaceAll = replaceAirOnly;
            return this;
        }

        /**
         * Set the y offset of the brush. Default: 0
         *
         * @param yOffset y offset of the brush
         * @return builder instance with applied offset
         */
        public BrushSettingsBuilder withYOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        /**
         * Set the placement method for the schematics. Default: {@link Placement#DROP}
         *
         * @param placement placement method for the schematic
         * @return builder instance with applied placement
         */
        public BrushSettingsBuilder withPlacementType(Placement placement) {
            this.placement = placement;
            return this;
        }

        /**
         * Build the brush configuration.
         *
         * @return A immutable brush config.
         */
        public BrushSettings build() {
            return new BrushSettings(brushes, includeAir, replaceAll, yOffset, placement);
        }

    }
}
