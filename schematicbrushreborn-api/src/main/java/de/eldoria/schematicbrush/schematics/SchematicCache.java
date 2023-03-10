/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * A cache which provides schematics based on filters or other factors.
 */
public interface SchematicCache {
    /**
     * The nameable key for the default cache
     */
    Nameable STORAGE = Nameable.of("storage");

    /**
     * Init method which will be executed when registered via {@link SchematicRegistry#register(Nameable, Object)}
     */
    void init();

    /**
     * Reload all schematics of the cache.
     */
    void reload();

    /**
     * Get a list of schematics which match a name or regex
     *
     * @param player player
     * @param name   name which will be parsed to a regex.
     * @return A brush config builder with assigned schematics.
     */
    Set<Schematic> getSchematicsByName(Player player, String name);

    /**
     * If a directory matches the full name, all schematics inside this directory will be returned directly.
     *
     * @param player the player requesting the schematics
     * @param name   name of the schematic. may be a regex
     * @param filter additional filter which may be an regex
     * @return all schematics inside the directory
     */
    Set<Schematic> getSchematicsByDirectory(Player player, String name, String filter);

    /**
     * Returns a list of matching directories.
     *
     * @param player the player requesting the schematics
     * @param dir    string for lookup
     * @param count  amount of returned directories
     * @return list of directory names with size of count or shorter
     */
    List<String> getMatchingDirectories(Player player, String dir, int count);

    /**
     * Returns a list of matching schematics.
     *
     * @param player the player requesting the schematics
     * @param name   string for lookup
     * @param count  amount of returned schematics
     * @return list of schematics names with size of count or shorter
     */
    List<String> getMatchingSchematics(Player player, String name, int count);

    /**
     * Get schematic count
     *
     * @return schematic count
     */
    int schematicCount();

    /**
     * Get directory count
     *
     * @return directory count
     */
    int directoryCount();

    /**
     * Called when the plugin gets shutdown and the cache is unregistered.
     */
    default void shutdown() {

    }
}
