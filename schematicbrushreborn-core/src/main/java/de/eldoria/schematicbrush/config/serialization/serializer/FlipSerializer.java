/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.serialization.serializer;

import de.eldoria.schematicbrush.brush.config.flip.Flip;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class FlipSerializer extends ValueSerializer<Flip> {
    @Override
    public void serialize(Flip value, JsonGenerator gen, SerializationContext context) {
        gen.writeString(value.name());
    }
}
