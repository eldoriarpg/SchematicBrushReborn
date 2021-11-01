package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;

@SuppressWarnings("unused")
public abstract class SchematicBrushReborn extends EldoPlugin {

    /**
     * Get schematic registry
     *
     * @return schematic registry
     */
    public abstract SchematicRegistry schematics();

    /**
     * Get brush settings registry
     *
     * @return brush settings registry
     */
    public abstract BrushSettingsRegistry brushSettingsRegistry();

    /**
     * Get the plugin config
     *
     * @return plugin config
     */
    public abstract Config config();
}
