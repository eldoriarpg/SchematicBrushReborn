package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface SchematicCache {
    Nameable DEFAULT_CACHE = Nameable.of("default");

    /**
     * Init method which will be executes when registered via {@link SchematicRegistry#register(Nameable, SchematicCache)}
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
     * @return all schematics inside the directory
     */
    Set<Schematic> getSchematicsByDirectory(Player player, String name, String filter);

    /**
     * Returns a list of matching directories.
     *
     * @param dir   string for lookup
     * @param count amount of returned directories
     * @return list of directory names with size of count or shorter
     */
    List<String> getMatchingDirectories(Player player, String dir, int count);

    /**
     * Returns a list of matching schematics.
     *
     * @param name  string for lookup
     * @param count amount of returned schematics
     * @return list of schematics names with size of count or shorter
     */
    List<String> getMatchingSchematics(Player player, String name, int count);

    /**
     * Get schematic count
     * @return schematic count
     */
    int schematicCount();

    /**
     * Get directory coun
     * @return directory count
     */
    int directoryCount();
}
