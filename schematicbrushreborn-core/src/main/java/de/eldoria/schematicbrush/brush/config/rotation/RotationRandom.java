/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class RotationRandom extends RotationList {
    public RotationRandom() {
        super(Arrays.asList(Rotation.ROT_ZERO, Rotation.ROT_RIGHT, Rotation.ROT_HALF, Rotation.ROT_LEFT));
    }

    public RotationRandom(Map<String, Object> objectMap) {
        this();
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
}
