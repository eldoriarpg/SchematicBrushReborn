package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.util.Placement;
import de.eldoria.schematicbrush.util.Randomable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A brush configuration represents the settings of a single brush.
 * A brush consists of one or more brushes represented by a {@link SubBrush} object.
 * If more than one {@link SubBrush} is present, a random {@link SubBrush} will be returned via
 * the {@link #getRandomBrushConfig()} based on the {@link SubBrush#getWeight()} of the brushes.
 * The brush settings contains some general brush settings,
 * which apply to the whole brush and not only to specific sub brushes.
 */
@Getter
public final class BrushConfiguration implements Randomable {
    /**
     * List of all sub brushes this brush has.
     */
    private final List<SubBrush> brushes;
    /**
     * True if the air of the schematic should replace non air blocks
     */
    private final boolean includeAir;
    /**
     * True if the schematic should only be pasted where the block material is {@link org.bukkit.Material#AIR}
     */
    private final boolean replaceAirOnly;
    /**
     * The y offset which will be applied before pasting to the position which was clicked by the user.
     */
    private final int yOffset;
    /**
     * Method which determins the origin of the schematic.
     */
    private final Placement placement;
    /**
     * The total weight of all brushes in the {@link #brushes} list
     */
    private final int totalWeight;

    private BrushConfiguration(List<SubBrush> brushes, boolean includeAir, boolean replaceAirOnly, int yOffset,
                               Placement placement) {
        this.brushes = brushes;
        this.includeAir = includeAir;
        this.replaceAirOnly = replaceAirOnly;
        this.yOffset = yOffset;
        this.placement = placement;

        // Count all weights, which have a weight set.
        int totalWeight = brushes.stream().filter(b -> b.getWeight() > 0).mapToInt(SubBrush::getWeight).sum();
        // Count all weighted brushes
        int weighted = (int) brushes.stream().filter(b -> b.getWeight() > 0).count();
        // Count all unweighted brushes
        int unweighted = (int) brushes.stream().filter(b -> b.getWeight() < 0).count();
        int defaultWeight;
        // Handle case, when no brush is weighted
        if (weighted == 0) {
            defaultWeight = 1;
        } else {
            // Calculate the default weight which is the average of all weightes brushes
            defaultWeight = totalWeight / weighted;
        }

        // Set the weight of all unweighted brushes
        brushes.stream().filter(b -> b.getWeight() < 0).forEach(b -> b.updateWeight(defaultWeight));

        // Calculate the total weight of all brushes
        this.totalWeight = brushes.stream().mapToInt(SubBrush::getWeight).sum();
    }

    /**
     * Get a random brush from the {@link #brushes} list based on their {@link SubBrush#getWeight()}.
     *
     * @return a random brush
     */
    public SubBrush getRandomBrushConfig() {
        int random = randomInt(totalWeight);

        int count = 0;
        for (SubBrush brush : brushes) {
            if (count + brush.getWeight() > random) {
                return brush;
            }
            count += brush.getWeight();
        }
        return brushes.get(brushes.size() - 1);
    }

    /**
     * Counts all schematics in all brushes. No deduplication.
     *
     * @return total number of schematics in all brushes.
     */
    public int getSchematicCount() {
        return brushes.stream().map(b -> b.getSchematics().size()).mapToInt(Integer::intValue).sum();
    }

    /**
     * Get a new builder for a brush configuration.
     *
     * @param brush brush which should be added
     * @return a brush builder with one brush added
     */
    public static Builder newSingleBrushSettingsBuilder(SubBrush brush) {
        return new Builder(brush);
    }

    /**
     * Get a new builder for a brush configuration.
     *
     * @return a brush builder without any configuration
     */
    public static Builder newBrushSettingsBuilder() {
        return new Builder();
    }

    /**
     * Get the brush configuration with a new brush combined.
     * The options from the current brush are used.
     *
     * @param brush Brush to combine. Only the {@link SubBrush} list is updated.
     * @return new brush configuration.
     */
    public BrushConfiguration combine(BrushConfiguration brush) {
        List<SubBrush> brushes = new ArrayList<>(this.brushes);
        brushes.addAll(brush.brushes);
        return new BrushConfiguration(brushes, includeAir, replaceAirOnly, yOffset, placement);
    }

    public static final class Builder {
        /**
         * List of all sub brushes this brush has.
         */
        private final List<SubBrush> brushes;
        /**
         * True if the air of the schematic should replace non air blocks
         */
        private boolean includeAir = false;
        /**
         * True if the schematic should only be pasted where the block material is {@link org.bukkit.Material#AIR}
         */
        private boolean replaceAirOnly = false;
        /**
         * The y offset which will be applied before pasting to the position which was clicked by the user.
         */
        private int yOffset = 0;
        /**
         * Method which determins the origin of the schematic.
         */
        private Placement placement = Placement.DROP;


        private Builder(SubBrush config) {
            brushes = List.of(config);
        }

        private Builder() {
            brushes = new ArrayList<>();
        }


        /**
         * Add a brush to the brush configuration.
         *
         * @param brush brush which should be added
         * @return builder instance with brush added
         */
        public Builder addBrush(SubBrush brush) {
            brushes.add(brush);
            return this;
        }

        /**
         * Defines if the brush should include air when pasting. Default: {@code false}
         *
         * @param includeAir True if the air of the schematic should replace non air blocks
         * @return builder instance with changed state
         */
        public Builder includeAir(boolean includeAir) {
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
        public Builder replaceAirOnly(boolean replaceAirOnly) {
            this.replaceAirOnly = replaceAirOnly;
            return this;
        }

        /**
         * Set the y offset of the brush. Default: 0
         *
         * @param yOffset y offset of the brush
         * @return builder instance with applied offset
         */
        public Builder withYOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        /**
         * Set the placement method for the schematics. Default: {@link Placement#DROP}
         *
         * @param placement placement method for the schematic
         * @return builder instance with applied placement
         */
        public Builder withPlacementType(Placement placement) {
            this.placement = placement;
            return this;
        }

        /**
         * Build the brush configuration.
         *
         * @return A immutable brush config.
         */
        public BrushConfiguration build() {
            return new BrushConfiguration(brushes, includeAir, replaceAirOnly, yOffset, placement);
        }

    }
}
