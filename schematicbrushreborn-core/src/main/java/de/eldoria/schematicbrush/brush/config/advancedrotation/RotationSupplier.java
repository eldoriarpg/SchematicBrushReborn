package de.eldoria.schematicbrush.brush.config.advancedrotation;

import de.eldoria.schematicbrush.brush.config.rotation.Rotation;

import java.util.concurrent.ThreadLocalRandom;

public class RotationSupplier {
    // TODO serialization
    private final Rotation x;
    private final Rotation y;
    private final Rotation z;

    public RotationSupplier(Rotation x, Rotation y, Rotation z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Rotation x() {
        return x;
    }

    public Rotation y() {
        return y;
    }

    public Rotation z() {
        return z;
    }

    public Rotation rotation() {
        return rotations.get(ThreadLocalRandom.current().nextInt(0, rotations.size()));
    }
}
