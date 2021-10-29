package de.eldoria.schematicbrush;

import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;

public abstract class SchematicBrushReborn extends EldoPlugin {

    public abstract SchematicRegistry schematics();

    public abstract BrushSettingsRegistry brushSettingsRegistry();

    public abstract Config config();
}
