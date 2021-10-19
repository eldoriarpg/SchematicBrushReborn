package de.eldoria.schematicbrush.brush.config.flip;

import java.util.Arrays;

public class FlipRandom extends FlipList {
    public FlipRandom() {
        super(Arrays.asList(Flip.NONE, Flip.EAST_WEST, Flip.NORTH_SOUTH));
    }
}
