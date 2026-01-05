/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.advancedrotation;

import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("sbrRotationFixed")
public class AdvancedRotationFixed extends AAdvancedRotation {
    public AdvancedRotationFixed(Rotation rotation, Rotation y, Rotation z) {
        super(rotation);
    }

    public AdvancedRotationFixed(Map<String, Object> objectMap) {
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
        return new AdvancedRotationFixed(rotation);
    }
}
