package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@SerializableAs("sbrPreset")
public class PresetImpl implements Preset {
    private final String name;
    private final List<SchematicSetBuilder> schematicSets;
    private String description;

    public PresetImpl(String name, List<SchematicSetBuilder> schematicSets) {
        this(name, "none", schematicSets);
    }

    public PresetImpl(String name, String description, List<SchematicSetBuilder> schematicSets) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.description = description;
        this.schematicSets = new ArrayList<>(schematicSets);
    }

    public PresetImpl(Map<String, Object> objectMap) {
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

    @Override
    public String name() {
        return name;
    }

    @Override
    public String infoComponent(boolean global) {
        return String.format("<%s><hover:show_text:'%s'>%s</hover> <%s><click:run_command:'/sbrp info %s'>[Info]</click>", Colors.NAME, simpleComponent(), name, Colors.ADD, (global ? "g:" : "") + name);
    }

    @Override
    public String detailComponent(boolean global) {
        var sets = schematicSets.stream()
                .map(set -> String.format("  <hover:show_text:'%s'>%s</hover>", set.infoComponent(), BuildUtil.renderProvider(set.selector())))
                        .collect(Collectors.toList());

        return MessageComposer.create()
                .text("<%s>Information about preset <%s>%s", Colors.HEADING, Colors.NAME, name)
                .newLine()
                .text("<%s>Description: <%s>%s <click:suggest_command:'/sbrp descr %s '><%s>[Change]</click>",
                        Colors.NAME, Colors.VALUE, description(), (global ? "g:" : "") + name, Colors.CHANGE)
                .newLine()
                .text("<%s>Schematic Sets:", Colors.NAME)
                .newLine()
                .text(sets)
                .build();
    }

    @Override
    public String simpleComponent() {
        var sets = schematicSets.stream()
                .map(set -> "  " + BuildUtil.renderProvider(set.selector()))
                .collect(Collectors.joining("\n"));

        var message = String.format("<%s>%s%n", Colors.VALUE, description());
        message += String.format("<%s>Schematic Sets:%n%s", Colors.NAME, sets);
        return message;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public void description(String description) {
        this.description = description;
    }

    @Override
    public List<SchematicSetBuilder> schematicSets() {
        return schematicSets.stream().map(SchematicSetBuilder::clone).collect(Collectors.toList());
    }
}
