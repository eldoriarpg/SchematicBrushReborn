/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class FlipRandom extends FlipList {
    public FlipRandom() {
        super(Arrays.asList(Flip.NONE, Flip.EAST_WEST, Flip.NORTH_SOUTH));
    }


    public FlipRandom(Map<String, Object> objectMap) {
        this();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
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
