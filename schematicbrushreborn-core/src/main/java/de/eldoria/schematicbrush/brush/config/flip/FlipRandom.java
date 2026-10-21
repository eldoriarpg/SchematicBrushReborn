/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.schematicbrush.brush.config.provider.Mutator;

import java.util.Arrays;

public class FlipRandom extends FlipList {
    public FlipRandom() {
        super(Arrays.asList(Flip.NONE, Flip.EAST_WEST, Flip.NORTH_SOUTH));
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
    public Mutator<Flip> copy() {
        return new FlipRandom();
    }
}
