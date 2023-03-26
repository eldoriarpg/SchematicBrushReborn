/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.brush;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilderSnapshot;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class used for serialization and saving of a {@link BrushBuilderSnapshot}/
 */
@SerializableAs("sbrBrush")
public class Brush implements ConfigurationSerializable, Comparable<Brush> {
    private final String name;

    protected String description;

    private final BrushBuilderSnapshot snapshot;

    /**
     * Constructs a new brush with the given snapshot.
     *
     * @param name     name of brush
     * @param snapshot snapshot of brush
     */
    @JsonCreator
    public Brush(@JsonProperty("name") String name,
                 @JsonProperty("description") String description,
                 @JsonProperty("snapshot") BrushBuilderSnapshot snapshot) {
        this.name = name;
        this.description = description;
        this.snapshot = snapshot;
    }

    /**
     * Constructs a new brush with the given brush.
     *
     * @param name    name of brush
     * @param builder brush builder
     */
    public Brush(String name, BrushBuilder builder) {
        this(name, "none", builder.snapshot());
    }

    /**
     * Constructs a new brush with the given brush.
     *
     * @param name     name of brush
     * @param snapshot snapshot
     */
    public Brush(String name, BrushBuilderSnapshot snapshot) {
        this(name, "none", snapshot);
    }

    /**
     * Constructor required by {@link ConfigurationSerializable} in order to deserialize the object.
     *
     * @param objectMap map of the already deserialized object
     */
    @SuppressWarnings("unused")
    public static Brush deserialize(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        String name = map.getValue("name");
        String description = map.getValue("description");
        if (description == null) {
            description = "none";
        }
        BrushBuilderSnapshot snapshot = map.getValue("snapshot");
        return new Brush(name, description, snapshot);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("snapshot", snapshot)
                .build();
    }

    /**
     * Name of brush
     *
     * @return name
     */
    public String name() {
        return name;
    }

    /**
     * Snapshot of the brush
     *
     * @return snapshot
     */
    public BrushBuilderSnapshot snapshot() {
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brush brush)) return false;

        if (!name.equals(brush.name)) return false;
        return snapshot.equals(brush.snapshot);
    }

    @Override
    public int hashCode() {
        var result = name.hashCode();
        result = 31 * result + snapshot.hashCode();
        return result;
    }

    public void description(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    @Override
    public int compareTo(@NotNull Brush o) {
        return name.compareTo(o.name);
    }

    public String simpleComponent(BrushSettingsRegistry registry) {
        var sets = snapshot.schematicSets().stream()
                .map(set -> "  " + BuildUtil.renderProvider(set.selector()))
                .toList();
        return MessageComposer.create()
                .text("<%s>%s", Colors.VALUE, description())
                .newLine()
                .text("<%s>Schematic Sets:", Colors.NAME)
                .newLine()
                .text(sets)
                .newLine()
                .text(simpleModifier(registry))
                .build();
    }

    public String infoComponent(boolean global, boolean canDelete, BrushSettingsRegistry registry) {
        var text = MessageComposer.create()
                .text("<%s><hover:show_text:'%s'>%s</hover>", Colors.NAME, simpleComponent(registry), name())
                .space()
                .text("<%s><click:run_command:'/sbrbp info %s'>[Info]</click>", Colors.ADD, (global ? "g:" : "") + name())
                .space()
                .text("<%s><click:run_command:'/sbr loadbrush %s'>[Load]</click>", Colors.ADD, (global ? "g:" : "") + name());
        if (canDelete) {
            text.space().text("<%s><click:run_command:'/sbrbp remove %s %s'>[Remove]</click>", Colors.REMOVE, name(), global ? "-g" : "");
        }

        return text.build();
    }

    private List<String> simpleModifier(BrushSettingsRegistry registry) {
        return snapshot.placementModifier().entrySet().stream()
                .map(e -> registry.getPlacementModifier(e.getKey())
                        .map(mod -> String.format("<%s>%s: <%s>%s",
                                Colors.NAME, mod.modifier().name(), Colors.VALUE, e.getValue().descriptor()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public String detailComponent(boolean global, BrushSettingsRegistry registry) {
        var sets = snapshot.schematicSets().stream()
                .map(set -> String.format("  <hover:show_text:'%s'>%s</hover>", set.infoComponent(), BuildUtil.renderProvider(set.selector())))
                .collect(Collectors.toList());

        var modifier = simpleModifier(registry);

        return MessageComposer.create()
                .text("<%s>Information about brush <%s>%s", Colors.HEADING, Colors.NAME, name())
                .newLine()
                .text("<%s>Description: <%s>%s <click:suggest_command:'/sbrbp descr %s '><%s>[Change]</click>",
                        Colors.NAME, Colors.VALUE, description(), (global ? "g:" : "") + name(), Colors.CHANGE)
                .newLine()
                .text("<%s>Schematic Sets:", Colors.NAME)
                .newLine()
                .text(sets)
                .newLine()
                .text(modifier)
                .build();
    }
}
