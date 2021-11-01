package de.eldoria.schematicbrush.brush.config.rotation;

import java.util.Map;

public class RotationFixed extends ARotation {
    public RotationFixed(Rotation rotation) {
        super(rotation);
    }

    public RotationFixed(Map<String, Object> objectMap) {
        super(objectMap);
    }


    @Override
    public void shift() {
        return rotation;
    }

    @Override
    public Rotation valueProvider() {
        return rotation;
    }

    @Override
    public String descriptor() {
        return String.format("%s", rotation.degree());
    }

    @Override
    public String name() {
        return "Fixed";
    }
}
