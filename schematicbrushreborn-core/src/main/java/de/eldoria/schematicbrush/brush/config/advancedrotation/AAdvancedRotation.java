/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.advancedrotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AAdvancedRotation implements Mutator<Rotation> {
    protected AdvancedRotation rotation = null;

    public AAdvancedRotation(AdvancedRotation rotation) {
        this.rotation = rotation;
    }

    public AAdvancedRotation(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        rotation = AdvancedRotation.valueOf(map.getValue("value"));
    }

    public AAdvancedRotation() {
    }

    public static AAdvancedRotation fixed(Rotation x, Rotation y, Rotation z) {
        return new AdvancedRotationFixed(x, y, z);
    }

    public static AAdvancedRotation list(List<RotationSupplier> rotations) {
        return new AdvancedRotationList(rotations);
    }

    public static AAdvancedRotation random(RandomRotationSupplier x, RandomRotationSupplier y, RandomRotationSupplier z) {
        return new AdvancedRotationRandom(x, y, z);
    }

    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                                .add("value", rotation.degree())
                                .build();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AAdvancedRotation aAdvancedRotation)) return false;

        return Objects.equals(rotation, aAdvancedRotation.rotation);
    }

    @Override
    public int hashCode() {
        return rotation != null ? rotation.hashCode() : 0;
    }
}
