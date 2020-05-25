package de.eldoria.schematicbrush.brush;

import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.util.Flip;
import de.eldoria.schematicbrush.util.Placement;
import de.eldoria.schematicbrush.util.Randomable;
import de.eldoria.schematicbrush.util.Rotation;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class BrushConfig implements Randomable {
    private final List<Schematic> schematics;
    private final String arguments;
    private final Rotation rotation;
    private final Flip flip;
    private final Placement placement;
    private int weight;

    public BrushConfig(List<Schematic> schematics, String arguments, Rotation rotation,
                       Flip flip, Placement placement, int weight) {
        this.schematics = schematics;
        this.arguments = arguments;
        this.rotation = rotation;
        this.flip = flip;
        this.placement = placement;
        this.weight = weight;
    }

    public void removeSchematic(Schematic schematic) {
        schematics.remove(schematic);
    }

    public Schematic getRandomSchematic() {
        if (schematics.isEmpty()) return null;
        return schematics.get(randomInt(schematics.size()));
    }

    public void updateWeight(int weight) {
        if (this.weight != -1) {
            throw new IllegalStateException("Weight can only be changed if its the default value");
        }

        if (weight < 1) {
            throw new IllegalArgumentException("Weight cant be less than 1");
        }

        this.weight = weight;
    }

    public static class Builder {
        private List<Schematic> schematics = Collections.emptyList();
        private String arguments;
        private Rotation rotation = Rotation.ROT_ZERO;
        private Flip flip = Flip.NONE;
        private Placement placement = Placement.DROP;
        private int weight = -1;

        public Builder(String arguments) {
            this.arguments = arguments;
        }

        public Builder withSchematics(List<Schematic> schematics) {
            this.schematics = schematics;
            return this;
        }

        public Builder withRotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder withFlip(Flip flip) {
            this.flip = flip;
            return this;
        }

        public Builder withPlacement(Placement placement) {
            this.placement = placement;
            return this;
        }

        public Builder withWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public BrushConfig build() {
            return new BrushConfig(schematics, arguments, rotation, flip, placement, weight);
        }
    }
}
