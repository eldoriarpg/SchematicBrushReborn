/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.schematicbrush.brush.config.provider.Mutator;

import java.util.Arrays;

public class RotationRandom extends RotationList {
    public RotationRandom() {
        super(Arrays.asList(Rotation.ROT_ZERO, Rotation.ROT_RIGHT, Rotation.ROT_HALF, Rotation.ROT_LEFT));
    }
    @Override
    public boolean shiftable() {
        return true;
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
        return new RotationRandom();
    }
}
