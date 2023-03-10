/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.command.tool.brush.Brush;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.history.BrushHistory;
import de.eldoria.schematicbrush.rendering.BlockChangeCollector;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A brush used to paste schematics.
 */
public interface SchematicBrush extends Brush {
    /**
     * Get the player associated with this brush
     *
     * @return player
     */
    Player brushOwner();

    /**
     * Get the bukkit player associated with this brush
     *
     * @return bukkit palyer
     */
    BukkitPlayer actor();

    @Override
    void build(EditSession editSession, BlockVector3 position, Pattern pattern, double size);

    /**
     * Paste the brush and capture changes.
     *
     * @return changes which will be made to the world
     */
    BlockChangeCollector pasteFake();

    /**
     * Get the location of the current brush
     *
     * @return location based on the conditions of {@link com.sk89q.worldedit.bukkit.BukkitPlayer#getBlockTraceFace(int, boolean, Mask)}
     */
    Optional<Location> getBrushLocation();

    /**
     * Get the settings of the brush
     *
     * @return settings
     */
    BrushSettings settings();

    /**
     * Get the next paste which will be executed
     *
     * @return next paste
     */
    @Nullable
    BrushPaste nextPaste();

    /**
     * Convert the settings of the brush to a builder
     *
     * @param settingsRegistry  settings registry
     * @param schematicRegistry schematic registry
     * @return brush as builder
     */
    BrushBuilder toBuilder(BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry);

    BrushHistory history();

    String info();
}
