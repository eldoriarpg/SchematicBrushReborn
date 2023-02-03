/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.flip;

import com.sk89q.worldedit.math.Vector3;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AFlip implements Mutator<Flip> {
    protected Flip flip = null;

    public AFlip() {
    }

    public AFlip(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        flip = Flip.valueOf(map.getValue("value"));
    }

    public AFlip(Flip flip) {
        this.flip = flip;
    }

    public static AFlip fixed(Flip flip) {
        return new FlipFixed(flip);
    }

    public static AFlip list(List<Flip> flips) {
        return new FlipList(flips);
    }

    public static AFlip random() {
        return new FlipRandom();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", flip.name())
                .build();
    }

    @Override
    public void value(@NotNull Flip value) {
        flip = value;
    }

    @Override
    public @NotNull Flip value() {
        return flip;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        if (value().direction() != Vector3.ZERO) {
            mutation.transform(mutation.transform().scale(value().direction().abs().multiply(-2.0).add(1.0, 1.0, 1.0)));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AFlip aFlip)) return false;

        return Objects.equals(flip, aFlip.flip);
    }

    @Override
    public int hashCode() {
        return flip != null ? flip.hashCode() : 0;
    }
}
