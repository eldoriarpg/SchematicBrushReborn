package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@SerializableAs("sbrPreset")
public class Preset implements ConfigurationSerializable {
    private final String name;
    private final List<SchematicSetBuilder> schematicSets;
    private String description;

    public Preset(String name, List<SchematicSetBuilder> schematicSets) {
        this(name, "none", schematicSets);
    }

    public Preset(String name, String description, List<SchematicSetBuilder> schematicSets) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.description = description;
        this.schematicSets = new ArrayList<>(schematicSets);
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
        return String.format("<%s><hover:show_text:'%s'>%s</hover> <%s><click:run_command:/sbrp info %s>[Info]</click>", Colors.NAME, simpleComponent(), name, Colors.ADD, (global ? "g:" : "") + name);
    }

    public String detailComponent() {
        var sets = schematicSets.stream()
                .map(set -> String.format("  <hover:show_text:'%s'>%s</hover>", set.infoComponent(), BuildUtil.renderProvider(set.selector())))
                .collect(Collectors.joining("\n"));

        var message = String.format("<%s>Information about preset <%s>%s%n", Colors.HEADING, Colors.NAME, name);
        message += String.format("<%s>Description: <%s>%s%n", Colors.NAME, Colors.VALUE, description());
        message += String.format("<%s>Schematic Sets:%n%s", Colors.NAME, sets);
        return message;
    }

    public String simpleComponent() {
        var sets = schematicSets.stream()
                .map(set -> "  " + BuildUtil.renderProvider(set.selector()))
                .collect(Collectors.joining("\n"));

        var message = String.format("<%s>%s%n", Colors.VALUE, description());
        message += String.format("<%s>Schematic Sets:%n%s", Colors.NAME, sets);
        return message;
    }

    public String description() {
        return description;
    }

    public void description(String description) {
        this.description = description;
    }

    public List<SchematicSetBuilder> schematicSets() {
        return schematicSets;
    }

}
