/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.schematicbrush.brush.config.provider.Mutator;

import java.util.Map;

public class FlipFixed extends AFlip {
    public FlipFixed(Flip flip) {
        super(flip);
    }

    public FlipFixed(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public Flip valueProvider() {
        return flip;
    }

    @Override
    public String descriptor() {
        return flip.name();
    }

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public Mutator<Flip> copy() {
        return new FlipFixed(flip);
    }
}
