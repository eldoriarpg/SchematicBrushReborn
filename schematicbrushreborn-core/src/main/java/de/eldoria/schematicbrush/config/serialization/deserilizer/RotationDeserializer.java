/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.serialization.deserilizer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;

import java.io.IOException;

public class RotationDeserializer extends JsonDeserializer<Rotation> {
    @Override
    public Rotation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return Rotation.valueOf(ctxt.readValue(p, Integer.class));
    }
}
