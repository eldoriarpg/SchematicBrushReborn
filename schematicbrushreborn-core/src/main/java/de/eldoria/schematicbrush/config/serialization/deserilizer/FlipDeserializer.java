/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.serialization.deserilizer;

import de.eldoria.schematicbrush.brush.config.flip.Flip;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class FlipDeserializer extends ValueDeserializer<Flip> {
    @Override
    public Flip deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        return Flip.valueOf(ctxt.readValue(p, String.class));
    }
}
