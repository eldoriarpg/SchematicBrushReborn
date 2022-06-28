/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("sbrRotationFixed")
public class RotationFixed extends ARotation {
    public RotationFixed(Rotation rotation) {
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
