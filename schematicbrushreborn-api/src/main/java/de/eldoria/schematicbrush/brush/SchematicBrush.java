package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.rendering.BlockChangeCollector;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;

public interface SchematicBrush extends Brush {
    @Override
    void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size);

    /**
     * Paste the brush and capture changes.
     *
     * @return changes which will be made to the world
     */
    BlockChangeCollector pasteFake();

    /**
     * Get the settings of the brush
     *
     * @return settings
     */
    BrushSettings getSettings();

    /**
     * Get the next paste which will be executed
     *
     * @return next paste
     */
    BrushPaste nextPaste();

    /**
     * Convert the settings of the brush to a builder
     *
     * @param settingsRegistry  settings registry
     * @param schematicRegistry schematic registry
     * @return brush as builder
     */
    BrushBuilder toBuilder(BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry);
}
