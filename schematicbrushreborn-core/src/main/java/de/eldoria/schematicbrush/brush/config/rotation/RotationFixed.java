/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.rotation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("sbrRotationFixed")
public class RotationFixed extends ARotation {
    @JsonCreator
    public RotationFixed(@JsonProperty("rotation") Rotation rotation) {
        super(rotation);
    }

    public RotationFixed(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public Rotation valueProvider() {
        return rotation;
    }

    @Override
    public String descriptor() {
        return String.format("%s", rotation.degree());
    }

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public Mutator<Rotation> copy() {
        return new RotationFixed(rotation);
    }
}
