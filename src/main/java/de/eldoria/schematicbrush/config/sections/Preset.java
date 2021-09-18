package de.eldoria.schematicbrush.config.sections;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@SerializableAs("sbrPreset")
public class Preset implements ConfigurationSerializable {
    private final String name;
    private String description;
    private List<String> filter;

    public Preset(String name, List<String> filter) {
        this(name, "none", filter);
    }

    public Preset(String name, String description, List<String> filter) {
        this.name = name;
        this.description = description;
        this.filter = filter;
    }

    public Preset(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        name = map.getValue("name");
        description = map.getValue("description");
        if (description == null) {
            description = "none";
        }
        filter = map.getValue("filter");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("description", description)
                .add("filter", filter)
                .build();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }
}
