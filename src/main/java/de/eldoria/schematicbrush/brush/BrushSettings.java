package de.eldoria.schematicbrush.brush;

import de.eldoria.schematicbrush.util.Placement;
import de.eldoria.schematicbrush.util.Randomable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BrushSettings implements Randomable {
    private final List<BrushConfig> brushes;
    private final boolean includeAir;
    private final boolean replaceAirOnly;
    private final int yOffset;
    private final Placement placement;
    private final int totalWeight;

    public BrushSettings(List<BrushConfig> brushes, boolean includeAir, boolean replaceAirOnly, int yOffset,
                         Placement placement) {
        this.brushes = brushes;
        this.includeAir = includeAir;
        this.replaceAirOnly = replaceAirOnly;
        this.yOffset = yOffset;
        this.placement = placement;

        // Count all weights, which have a weight set.
        int totalWeight = brushes.stream().filter(b -> b.getWeight() > 0).mapToInt(BrushConfig::getWeight).sum();
        int weighted = (int) brushes.stream().filter(b -> b.getWeight() > 0).count();
        int unweighted = (int) brushes.stream().filter(b -> b.getWeight() < 0).count();
        int defaultWeight;
        if (weighted == 0) {
            defaultWeight = 1;
        } else {
            defaultWeight = totalWeight / weighted;
        }

        brushes.stream().filter(b -> b.getWeight() < 0).forEach(b -> b.updateWeight(defaultWeight));

        this.totalWeight = brushes.stream().mapToInt(BrushConfig::getWeight).sum();
    }

    public BrushConfig getRandomBrushConfig() {
        int random = randomInt(totalWeight);

        int count = 0;
        for (BrushConfig brush : brushes) {
            if (count + brush.getWeight() > random) {
                return brush;
            }
            count += brush.getWeight();
        }
        return brushes.get(brushes.size() - 1);
    }

    public static Builder newSingleBrushSettingsBuilder(BrushConfig config) {
        return new Builder(config);
    }

    public static Builder newBrushSettingsBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final List<BrushConfig> brushes;
        private boolean includeAir = false;
        private boolean replaceAirOnly = false;
        private int yOffset = 0;
        private Placement placement = Placement.DROP;

        public Builder(BrushConfig config) {
            brushes = List.of(config);
        }

        public Builder() {
            brushes = new ArrayList<>();
        }


        public Builder addBrush(BrushConfig config) {
            brushes.add(config);
            return this;
        }

        public Builder includeAir(boolean includeAir) {
            this.includeAir = includeAir;
            return this;
        }

        public Builder replaceAirOnly(boolean replaceAirOnly) {
            this.replaceAirOnly = replaceAirOnly;
            return this;
        }

        public Builder withYOffset(int yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        public Builder withPlacementType(Placement placement) {
            this.placement = placement;
            return this;
        }

        public BrushSettings build() {
            return new BrushSettings(brushes, includeAir, replaceAirOnly, yOffset, placement);
        }

    }
}
