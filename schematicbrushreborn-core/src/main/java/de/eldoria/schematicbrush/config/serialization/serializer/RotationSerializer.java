/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.serialization.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;

import java.io.IOException;

public class RotationSerializer extends JsonSerializer<Rotation> {
    @Override
    public void serialize(Rotation value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.degree());
    }
}
