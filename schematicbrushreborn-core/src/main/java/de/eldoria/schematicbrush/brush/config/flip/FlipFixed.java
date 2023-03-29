/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.flip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("sbrFlipFixed")
public class FlipFixed extends AFlip {
    @JsonCreator
    public FlipFixed(@JsonProperty("flip") Flip flip) {
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
