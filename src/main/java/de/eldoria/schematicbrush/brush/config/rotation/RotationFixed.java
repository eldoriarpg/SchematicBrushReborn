package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RotationFixed extends ARotation {
    public RotationFixed(Rotation rotation) {
        this.rotation = rotation;
    }

    public RotationFixed(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        rotation = Rotation.asRotation(map.getValueOrDefault("value", 0));
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", value().degree())
                .build();
    }


    @Override
    public Rotation shift() {
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
