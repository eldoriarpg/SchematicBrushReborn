package de.eldoria.schematicbrush.brush.config.rotation;

public class RotationFixed extends ARotation {
    public RotationFixed(Rotation rotation) {
        this.rotation = rotation;
    }

    @Override
    public Rotation shift() {
        return rotation;
    }

    @Override
    public Rotation valueProvider() {
        return rotation;
    }
}
