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
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilderImpl;

import java.io.IOException;

public class SchematicSetBuilderDeserializer extends JsonDeserializer<SchematicSetBuilder> {
    @Override
    public SchematicSetBuilder deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return ctxt.readValue(p, SchematicSetBuilderImpl.class);
    }
}
