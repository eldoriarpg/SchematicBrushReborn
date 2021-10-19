package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;

import java.util.List;

public abstract class ARotation implements IShiftable<Rotation> {
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

}
