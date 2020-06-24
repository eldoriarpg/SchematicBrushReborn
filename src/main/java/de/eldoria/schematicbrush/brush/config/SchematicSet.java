package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.parameter.Flip;
import de.eldoria.schematicbrush.brush.config.parameter.Rotation;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.util.Randomable;
import lombok.Getter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * The schematic set represents a part of a brush,
 * which will be combined to a brush by the {@link BrushSettings}
 * A brush can have a weight which indicates how often it should be used,
 * when a {@link BrushSettings} contains more than one brush.
 * The brush contains one ore more schematics which will be provided by random.
 */
@Getter
public class SchematicSet implements Randomable {
    /**
     * A list of schematics.
     */
    private final List<Schematic> schematics;
    /**
     * The arguments which were used to create this brush
     */
    private final String arguments;
    /**
     * Rotation of the schematic.
     */
    //TODO: Allow multiple rotations for more specific randomized pasting
    private final Rotation rotation;
    /**
     * Flip direction of the schematic
     */
    //TODO: Allow multiple flap values for more specific randomized pasting
    private final Flip flip;
    /**
     * Weight of the brush. Must be always larger then 1. Is -1 when no weight is applied.
     */
    private int weight;

    public SchematicSet(List<Schematic> schematics, String arguments, Rotation rotation,
                        Flip flip, int weight) {
        this.schematics = schematics;
        this.arguments = arguments;
        this.rotation = rotation;
        this.flip = flip;
        this.weight = weight;
    }

    public Clipboard getRandomSchematic() {
        if (schematics.isEmpty()) return null;

        Clipboard clipboard = null;

        Schematic randomSchematic;
        // Search for loadable schematic. Should be likely always the first one.
        while (clipboard == null && !schematics.isEmpty()) {
            randomSchematic = schematics.get(randomInt(schematics.size()));
            try {
                clipboard = randomSchematic.getSchematic();
            } catch (IOException e) {
                // Silently fail and search for another schematic.
                SchematicBrushReborn.logger().info("Schematic \"" + randomSchematic.getPath() + "\" does not exist anymore.");
                schematics.remove(randomSchematic);
            }
        }

        return clipboard;
    }

    /**
     * Update a not weighted brush.
     *
     * @param weight weight to set.
     * @throws IllegalStateException    when the weight is not -1 and this method is called.
     * @throws IllegalArgumentException when the weight is less than 1.
     */
    public void updateWeight(int weight) throws IllegalStateException, IllegalArgumentException {
        if (this.weight != -1) {
            throw new IllegalStateException("Weight can only be changed if it's the default value.");
        }

        if (weight < 1) {
            throw new IllegalArgumentException("Weight can't be less than 1");
        }

        this.weight = weight;
    }

    /**
     * This class is a builder to build a {@link SchematicSet}.
     */
    public static class SchematicSetBuilder {
        private List<Schematic> schematics = Collections.emptyList();
        private final String arguments;
        private Rotation rotation = Rotation.ROT_ZERO;
        private Flip flip = Flip.NONE;
        private int weight = -1;

        public SchematicSetBuilder(String arguments) {
            this.arguments = arguments;
        }

        /**
         * Set the schematic list of brush.
         *
         * @param schematics schematics to set
         * @return instance with schematics set
         */
        public SchematicSetBuilder withSchematics(List<Schematic> schematics) {
            this.schematics = schematics;
            return this;
        }

        /**
         * Set the rotation of the brush.
         *
         * @param rotation rotation of the brush
         * @return instance with rotation set.
         */
        public SchematicSetBuilder withRotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        /**
         * Set the flip of the brush.
         *
         * @param flip flip of the brush
         * @return instance with flip set
         */
        public SchematicSetBuilder withFlip(Flip flip) {
            this.flip = flip;
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
         * @return new sub brush instance with set values and default if not set.
         */
        public SchematicSet build() {
            return new SchematicSet(schematics, arguments, rotation, flip, weight);
        }
    }
}
