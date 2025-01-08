/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.advancedrotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

@SerializableAs("sbrRotationRandom")
public class AdvancedRotationRandom extends AdvancedRotationList {
    public AdvancedRotationRandom(RandomRotationSupplier x, RandomRotationSupplier y, RandomRotationSupplier z) {
        super(Arrays.asList(Rotation.ROT_ZERO, Rotation.ROT_RIGHT, Rotation.ROT_HALF, Rotation.ROT_LEFT));
    }

    public AdvancedRotationRandom(Map<String, Object> objectMap) {
        this(x, y, z);
    }

    @Override
    public boolean shiftable() {
        return true;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder().build();
    }

    @Override
    public String descriptor() {
        return "";
    }

    @Override
    public String name() {
        return "Random";
    }

    @Override
    public Mutator<Rotation> copy() {
        return new AdvancedRotationRandom(x, y, z);
    }
}
