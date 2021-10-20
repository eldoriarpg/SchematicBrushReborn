package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.PasteMutation;

import java.util.List;

public abstract class ARotation implements Mutator<Rotation> {
    protected Rotation rotation;

    public static ARotation fixed(Rotation rotation) {
        return new RotationFixed(rotation);
    }

    public static ARotation list(List<Rotation> rotations) {
        return new RotationList(rotations);
    }

    public static ARotation random() {
        return new RotationRandom();
    }

    @Override
    public void value(Rotation value) {
        rotation = value;
    }

    @Override
    public Rotation value() {
        return rotation;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        if (rotation.value().degree() != 0) {
            mutation.transform(mutation.transform().rotateY(rotation.value().degree()));
        }
    }
}
