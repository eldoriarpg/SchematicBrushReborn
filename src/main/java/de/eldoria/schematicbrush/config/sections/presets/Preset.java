package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.config.PresetContainer;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
        this.name = name.toLowerCase(Locale.ROOT);
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

    public String infoComponent(boolean global) {
        return String.format("<%s>%s <%s><click:run_command:/preset info %s>[Info]</click>", Colors.NAME, name, Colors.ADD, (global ? "g:" : "") + name);
    }

    public String detailComponent(){
        List<String> sets = new ArrayList<>();
        for (var set : schematicSets) {
            sets.add(String.format("<hover:show_text:'%s'>%s</hover>", set.infoComponent(), set.selector().descriptor()));
        }

        var message = String.format("<%s>Information about preset %s", Colors.HEADING, Colors.NAME);
        message += String.format("<%s>Description: <%s>%s", Colors.NAME, Colors.VALUE, description());
        message += String.format("<%s>Schematic Sets:%n%s", String.join("\n", sets));
        return message;
    }

    public String description() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SchematicSetBuilder> schematicSets() {
        return schematicSets;
    }

}
