package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class ARotation implements Mutator<Rotation> {
    protected Rotation rotation;

    public ARotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public ARotation(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        rotation = Rotation.valueOf(map.getValue("value"));
    }

    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", rotation.degree())
                .build();
    }

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
