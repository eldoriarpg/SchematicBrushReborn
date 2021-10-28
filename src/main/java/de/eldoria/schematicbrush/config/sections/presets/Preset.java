package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@SerializableAs("sbrPreset")
public class Preset implements ConfigurationSerializable {
    private final String name;
    private String description;
    private List<SchematicSetBuilder> schematicSets;

    public Preset(String name, List<SchematicSetBuilder> schematicSets) {
        this(name, "none", schematicSets);
    }

    public Preset(String name, String description, List<SchematicSetBuilder> schematicSets) {
        this.name = name;
        this.description = description;
        this.schematicSets = schematicSets;
    }

    public Preset(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        description = map.getValue("description");
        if (description == null) {
            description = "none";
        }
        schematicSets = map.getValue("sets");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("description", description)
                .add("sets", schematicSets)
                .build();
    }

    public String name() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SchematicSetBuilder> schematicSets() {
        return schematicSets;
    }
}
