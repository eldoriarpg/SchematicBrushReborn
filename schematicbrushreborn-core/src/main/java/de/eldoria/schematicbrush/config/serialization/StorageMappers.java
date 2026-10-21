/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import de.eldoria.jacksonbukkit.JacksonPaper;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import de.eldoria.schematicbrush.config.serialization.deserilizer.BrushBuilderSnapshotDeserializer;
import de.eldoria.schematicbrush.config.serialization.deserilizer.FlipDeserializer;
import de.eldoria.schematicbrush.config.serialization.deserilizer.RotationDeserializer;
import de.eldoria.schematicbrush.config.serialization.deserilizer.SchematicSetBuilderDeserializer;
import de.eldoria.schematicbrush.config.serialization.serializer.FlipSerializer;
import de.eldoria.schematicbrush.config.serialization.serializer.RotationSerializer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

/**
 * Builds the modules and mappers that read and write the schematic brush configuration and storage files.
 */
public final class StorageMappers {
    private StorageMappers() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Creates the module for Paper types.
     *
     * @return new module
     */
    public static JacksonModule platformModule() {
        return JacksonPaper.builder()
                           .colorAsHex()
                           .build();
    }

    /**
     * Creates the module with the serializers and deserializers for schematic brush types.
     *
     * @return new mutable module
     */
    public static SimpleModule schematicBrushModule() {
        var module = new SimpleModule();
        module.addSerializer(Flip.class, new FlipSerializer());
        module.addSerializer(Rotation.class, new RotationSerializer());
        module.addDeserializer(Flip.class, new FlipDeserializer());
        module.addDeserializer(Rotation.class, new RotationDeserializer());
        module.addDeserializer(SchematicSetBuilder.class, new SchematicSetBuilderDeserializer());
        module.addDeserializer(BrushBuilderSnapshot.class, new BrushBuilderSnapshotDeserializer());
        return module;
    }

    /**
     * Configures a mapper for schematic brush and Paper objects.
     *
     * @param builder         builder to configure
     * @param classLoader     class loader that resolves the stored class names
     * @param platformModule  module for Paper types
     * @param schematicModule module for schematic brush types
     * @return the built mapper
     */
    public static ObjectMapper configure(MapperBuilder<?, ?> builder, ClassLoader classLoader, JacksonModule platformModule, JacksonModule schematicModule) {
        builder.addModule(platformModule)
               .typeFactory(TypeFactory.createDefaultInstance().withClassLoader(classLoader))
               .addModule(schematicModule)
               .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
               .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
               .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
               .changeDefaultVisibility(visibility -> visibility.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                                                                .withGetterVisibility(JsonAutoDetect.Visibility.NONE));
        if (builder instanceof YAMLMapper.Builder yaml) {
            yaml.disable(YAMLWriteFeature.USE_NATIVE_TYPE_ID)
                .disable(YAMLWriteFeature.WRITE_DOC_START_MARKER);
        }
        return builder.build();
    }
}
