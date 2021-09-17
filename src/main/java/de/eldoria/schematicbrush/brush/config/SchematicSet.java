package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.flip.AFlip;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.rotation.ARotation;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.util.Randomable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The schematic set represents a part of a brush, which will be combined to a brush by the {@link BrushSettings} A
 * brush can have a weight which indicates how often it should be used, when a {@link BrushSettings} contains more than
 * one brush. The brush contains one ore more schematics which will be provided by random.
 */
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
    private final ARotation rotation;
    /**
     * Flip direction of the schematic
     */
    //TODO: Allow multiple flap values for more specific randomized pasting
    private final AFlip flip;
    /**
     * Weight of the brush. Must be always larger then 1. Is -1 when no weight is applied.
     */
    private int weight;

    public SchematicSet(Set<Schematic> schematics, String arguments, ARotation rotation,
                        AFlip flip, int weight) {
        this.schematics = new ArrayList<>(schematics);
        this.arguments = arguments;
        this.rotation = rotation;
        this.flip = flip;
        this.weight = weight;
    }

    public Schematic getRandomSchematic() {
        if (schematics.isEmpty()) return null;

        Clipboard clipboard = null;

        Schematic randomSchematic = null;
        // Search for loadable schematic. Should be likely always the first one.
        while (clipboard == null && !schematics.isEmpty()) {
            randomSchematic = schematics.get(randomInt(schematics.size()));
            try {
                clipboard = randomSchematic.loadSchematic();
            } catch (IOException e) {
                // Silently fail and search for another schematic.
                SchematicBrushReborn.logger().info("Schematic \"" + randomSchematic.path() + "\" does not exist anymore.");
                schematics.remove(randomSchematic);
            }
        }

        return randomSchematic;
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

    public List<Schematic> schematics() {
        return schematics;
    }

    public String arguments() {
        return arguments;
    }

    public ARotation rotation() {
        return rotation;
    }

    public AFlip flip() {
        return flip;
    }

    public int weight() {
        return weight;
    }

    /**
     * This class is a builder to build a {@link SchematicSet}.
     */
    public static class SchematicSetBuilder {
        private final String arguments;
        private Set<Schematic> schematics = Collections.emptySet();
        private ARotation rotation = ARotation.fixed(Rotation.ROT_ZERO);
        private AFlip flip = AFlip.fixed(Flip.NONE);
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
        public SchematicSetBuilder withSchematics(Set<Schematic> schematics) {
            this.schematics = schematics;
            return this;
        }

        /**
         * Set the rotation of the brush.
         *
         * @param rotation rotation of the brush
         * @return instance with rotation set.
         */
        public SchematicSetBuilder withRotation(ARotation rotation) {
            this.rotation = rotation;
            return this;
        }

        /**
         * Set the flip of the brush.
         *
         * @param flip flip of the brush
         * @return instance with flip set
         */
        public SchematicSetBuilder withFlip(AFlip flip) {
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
         *
         * @return new sub brush instance with set values and default if not set.
         */
        public SchematicSet build() {
            return new SchematicSet(schematics, arguments, rotation, flip, weight);
        }
    }
}
