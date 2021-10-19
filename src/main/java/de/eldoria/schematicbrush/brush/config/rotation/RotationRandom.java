package de.eldoria.schematicbrush.brush.config.rotation;

import java.util.Arrays;

public class RotationRandom extends RotationList {
    public RotationRandom() {
        super(Arrays.asList(Rotation.ROT_ZERO, Rotation.ROT_RIGHT, Rotation.ROT_HALF, Rotation.ROT_LEFT));
    }
}
