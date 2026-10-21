/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.serialization.serializer;

import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class RotationSerializer extends ValueSerializer<Rotation> {
    @Override
    public void serialize(Rotation value, JsonGenerator gen, SerializationContext context) {
        gen.writeNumber(value.degree());
    }
}
