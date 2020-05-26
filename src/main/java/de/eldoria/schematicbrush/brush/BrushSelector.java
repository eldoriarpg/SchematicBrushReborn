package de.eldoria.schematicbrush.brush;

public enum BrushSelector {
    /**
     * Indicates that this brush selects schematic with a regex or regex like string.
     */
    REGEX,

    /**
     * Indicates that this brush selects all schematics inside a directory.
     */
    DIRECTORY,
    /**
     * Indicates that this brush selects all brushes inside a preset.
     */
    PRESET
}
