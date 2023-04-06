/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.eldoria.eldoutilities.core.EldoUtilities;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * The base of the schematic brush plugin.
 */
@SuppressWarnings("unused")
public abstract class SchematicBrushReborn extends EldoPlugin {

    public SchematicBrushReborn() {
        EldoUtilities.forceInstanceOwner(this);
    }

    public SchematicBrushReborn(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
        EldoUtilities.forceInstanceOwner(this);
    }

    /**
     * Get the instance of the plugin.
     *
     * @return the current plugin instance.
     */
    public static SchematicBrushReborn instance() {
        return (SchematicBrushReborn) getInstance();
    }

    public abstract Module platformModule();

    public abstract SimpleModule schematicBrushModule();

    public abstract ObjectMapper configureMapper(MapperBuilder<?, ?> builder);

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
     * Get the storage registry
     *
     * @return storage registry
     */
    public abstract StorageRegistry storageRegistry();

    /**
     * Get the plugin config
     *
     * @return plugin config
     */
    public abstract Configuration config();
}
