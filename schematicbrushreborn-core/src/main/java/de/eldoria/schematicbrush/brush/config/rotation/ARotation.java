/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ARotation implements Mutator<Rotation> {
    protected Rotation rotation = null;

    public ARotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public ARotation(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        rotation = Rotation.valueOf(map.getValue("value"));
    }

    public ARotation() {
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

    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", rotation.degree())
                .build();
    }

    @Override
    public void value(@NotNull Rotation value) {
        rotation = value;
    }

    @Override
    public @NotNull Rotation value() {
        return rotation;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        if (rotation.value().degree() != 0) {
            mutation.transform(mutation.transform().rotateY(rotation.value().degree()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ARotation aRotation)) return false;

        return Objects.equals(rotation, aRotation.rotation);
    }

    @Override
    public int hashCode() {
        return rotation != null ? rotation.hashCode() : 0;
    }
}
