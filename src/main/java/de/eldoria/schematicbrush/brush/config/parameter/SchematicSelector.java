package de.eldoria.schematicbrush.brush.config.parameter;

import de.eldoria.schematicbrush.brush.config.selector.Selector;

/**
 * @deprecated Replaced by {@link Selector}
 */
@Deprecated(forRemoval = true)
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
