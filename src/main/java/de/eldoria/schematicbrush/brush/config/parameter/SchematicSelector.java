package de.eldoria.schematicbrush.brush.config.parameter;

public enum SchematicSelector {
    /**
     * Indicates that this set selects schematic with a regex or regex like string.
     */
    REGEX,

    /**
     * Indicates that this set selects all schematics inside a directory.
     */
    DIRECTORY,
    /**
     * Indicates that this set selects all brushes inside a preset.
     */
    PRESET
}
