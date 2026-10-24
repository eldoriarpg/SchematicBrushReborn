package de.eldoria.schematicbrush.brush.config.advancedrotation;

import de.eldoria.schematicbrush.brush.config.rotation.Rotation;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomRotationSupplier {
    // TODO serialization
    private final List<Rotation> rotations;

    public RandomRotationSupplier(List<Rotation> rotations) {
        this.rotations = rotations;
    }

    public Rotation rotation() {
        return rotations.get(ThreadLocalRandom.current().nextInt(0, rotations.size()));
    }
}
