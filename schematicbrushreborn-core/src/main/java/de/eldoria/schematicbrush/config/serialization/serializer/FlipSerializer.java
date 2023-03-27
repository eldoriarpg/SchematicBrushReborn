package de.eldoria.schematicbrush.config.serialization.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.eldoria.schematicbrush.brush.config.flip.Flip;

import java.io.IOException;

public class FlipSerializer extends JsonSerializer<Flip> {
    @Override
    public void serialize(Flip value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.name());
    }
}
