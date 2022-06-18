/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.preset;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representing a schematic preset which holds multiple schematic sets.
 */
public abstract class Preset implements ConfigurationSerializable {
    protected final String name;
    protected final List<SchematicSetBuilder> schematicSets;
    protected String description;

    public Preset(String name, List<SchematicSetBuilder> schematicSets, String description) {
        this.name = name;
        this.schematicSets = schematicSets;
        this.description = description == null ? "none" : description;
    }

    /**
     * Name of the preset. This is unique for each container
     *
     * @return name
     */
    public String name() {
        return name;
    }

    public String infoComponent(boolean global, boolean canDelete) {
        var text = MessageComposer.create()
                .text("<%s><hover:show_text:'%s'>%s</hover>", Colors.NAME, simpleComponent(), name())
                .space()
                .text("<%s><click:run_command:'/sbrp info %s'>[Info]</click>", Colors.ADD, (global ? "g:" : "") + name());
        if (canDelete) {
            text.space()
                    .text("<%s><click:run_command:'/sbrp remove %s %s'>[Remove]</click>", Colors.REMOVE, name(), (global ? "-g" : ""));
        }

        return text.build();
    }

    public String detailComponent(boolean global) {
        var sets = schematicSets().stream()
                .map(set -> String.format("  <hover:show_text:'%s'>%s</hover>", set.infoComponent(), BuildUtil.renderProvider(set.selector())))
                .collect(Collectors.toList());

        return MessageComposer.create()
                .text("<%s>Information about preset <%s>%s", Colors.HEADING, Colors.NAME, name())
                .newLine()
                .text("<%s>Description: <%s>%s <click:suggest_command:'/sbrp descr %s '><%s>[Change]</click>",
                        Colors.NAME, Colors.VALUE, description(), (global ? "g:" : "") + name(), Colors.CHANGE)
                .newLine()
                .text("<%s>Schematic Sets:", Colors.NAME)
                .newLine()
                .text(sets)
                .build();
    }

    public String simpleComponent() {
        var sets = schematicSets().stream()
                .map(set -> "  " + BuildUtil.renderProvider(set.selector()))
                .collect(Collectors.joining("\n"));

        var message = String.format("<%s>%s%n", Colors.VALUE, description());
        message += String.format("<%s>Schematic Sets:%n%s", Colors.NAME, sets);
        return message;
    }

    /**
     * Description of the preset
     *
     * @return description
     */
    public String description() {
        return description;
    }

    /**
     * Set the description
     *
     * @param description the description
     */
    public void description(String description) {
        this.description = description;
    }

    /**
     * Get a copy of all schematic set builders contained in the preset
     *
     * @return list of presets
     */
    public List<SchematicSetBuilder> schematicSetsCopy() {
        return schematicSets.stream().map(SchematicSetBuilder::copy).collect(Collectors.toList());
    }

    /**
     * Get all schematic set builders contained in the preset
     *
     * @return list of presets
     */
    public List<SchematicSetBuilder> schematicSets() {
        return Collections.unmodifiableList(schematicSets);
    }
}
