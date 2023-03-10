/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.schematics;

import de.eldoria.eldoutilities.utils.TextUtil;
import de.eldoria.schematicbrush.SchematicBrushRebornImpl;
import de.eldoria.schematicbrush.config.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class SchematicBrushCache implements SchematicCache {
    private static final Pattern UUID_PATTERN = Pattern.compile("[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}");
    private static final Logger logger = SchematicBrushRebornImpl.logger();
    private final JavaPlugin plugin;
    private final Configuration configuration;
    private final Map<String, Set<Schematic>> schematicsCache = new HashMap<>();
    private final Map<UUID, Map<String, Set<Schematic>>> userCache = new HashMap<>();
    private SchematicWatchService watchService;

    public SchematicBrushCache(JavaPlugin plugin, Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    @Override
    public void init() {
        reload();
        watchService = SchematicWatchService.of(plugin, configuration, this);
    }

    /**
     * Reload the current loaded schematics. This overrides the cache, when the schematics are loaded.
     */
    @Override
    public void reload() {
        plugin.getLogger().log(Level.CONFIG, "Reloading schematics.");

        var root = plugin.getDataFolder().toPath().getParent().toString();

        schematicsCache.clear();

        for (var source : configuration.schematicConfig().sources()) {
            var path = source.path();
            if (path == null || path.isEmpty()) {
                plugin.getLogger().log(Level.CONFIG, "Path " + source + " has no path. Skipping!");
                continue;
            }

            path = path.replace("\\", "/");

            var load = source.isRelative() ? Paths.get(root, path) : Paths.get(path);
            loadSchematics(load);
        }
    }

    private void loadSchematics(Path schematicFolder) {
        // fail silently if this folder does not exist.
        if (!schematicFolder.toFile().exists()) {
            logger.config(schematicFolder.toString() + " does not exist. Skipping.");
            return;
        }

        var baseDirectoryData = getDirectoryData(schematicFolder);

        if (baseDirectoryData.isEmpty()) {
            logger.warning("Could not load schematics from " + schematicFolder + " folder.");
            return;
        }

        logger.log(Level.CONFIG, "Loading schematics from " + schematicFolder);

        // initialise queue with directory in first layer
        Queue<Path> deepDirectories = new ArrayDeque<>(baseDirectoryData.get().directories());
        deepDirectories.add(schematicFolder.toFile().toPath());

        // iterate over every directory.
        // load files and add new directories if found.
        while (!deepDirectories.isEmpty()) {
            var path = deepDirectories.poll();

            var directoryData = getDirectoryData(path);
            if (directoryData.isEmpty()) {
                continue;
            }
            // Queue new directories
            deepDirectories.addAll(directoryData.get().directories());

            // Build schematic references
            for (var file : directoryData.get().files()) {
                addSchematic(file.toPath());
            }
            logger.log(Level.CONFIG, "Loaded " + directoryData.get().files().size() + "schematics from " + path.toString());
        }
        logger.log(Level.CONFIG, "Loaded schematics from " + schematicFolder);
    }

    public void removeSchematic(File file) {
        if (file.isDirectory()) {
            return;
        }

        for (var value : schematicsCache.values()) {
            value.removeIf(schematic -> schematic.getFile().equals(file));
        }
    }

    public void addSchematic(Path file) {
        var directory = file.getParent();

        var sourceForPath = configuration.schematicConfig().getSourceForPath(directory);

        if (sourceForPath.isEmpty()) {
            logger.log(Level.CONFIG, "File " + directory + " is not part of a source");
            return;
        }

        var source = sourceForPath.get();

        if (source.isExcluded(directory)) {
            logger.log(Level.CONFIG, "Directory " + directory + "is excluded.");
            return;
        }

        // remove path to get relative path in schematic folder.
        var rawKey = source.internalPath(directory).toString();

        String cleanKey;
        if (!rawKey.isEmpty()) {
            cleanKey = rawKey.replace(" ", "_").replace("\\", configuration.schematicConfig().pathSeparator());
        } else {
            cleanKey = rawKey;
        }

        UUID playerUid = null;

        var matcher = UUID_PATTERN.matcher(rawKey);
        if (matcher.find()) {
            var uuidString = matcher.group();
            logger.log(Level.CONFIG, "Found UUID " + uuidString);
            playerUid = UUID.fromString(uuidString);
            cleanKey = cleanKey.replaceFirst(uuidString + "/?", "");
        }

        if (configuration.schematicConfig().isPathSourceAsPrefix()) {
            cleanKey = source.prefix() + configuration.schematicConfig().pathSeparator() + cleanKey;
        }

        Schematic schematic;
        try {
            schematic = Schematic.of(file);
        } catch (InvalidClipboardFormatException e) {
            logger.log(Level.WARNING, "Format of " + file + " is invalid.");
            return;
        }

        if (playerUid != null) {
            userCache.computeIfAbsent(playerUid, key -> new HashMap<>())
                    .computeIfAbsent(cleanKey, key -> new HashSet<>())
                    .add(schematic);
        } else {
            schematicsCache.computeIfAbsent(cleanKey, key -> new HashSet<>()).add(schematic);
        }
    }

    private Optional<DirectoryData> getDirectoryData(Path directory) {
        List<Path> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();

        // Get a list of all files and directories in a directory
        try (var paths = Files.list(directory)) {
            // Check for each file if it's a directory or a file.
            for (var path : paths.toList()) {
                if (path.equals(directory)) continue;
                var file = path.toFile();
                var attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                if (attributes.isDirectory()) {
                    directories.add(path);
                } else if (attributes.isRegularFile()) {
                    files.add(file);
                }
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(new DirectoryData(directories, files));
    }

    /**
     * Get a list of schematics which match a name or regex
     *
     * @param player player
     * @param name   name which will be parsed to a regex.
     * @return A brush config builder with assigned schematics.
     */
    @Override
    public Set<Schematic> getSchematicsByName(Player player, String name) {
        return filterSchematics(getSchematics(player), name);
    }

    private Set<Schematic> filterSchematics(Set<Schematic> schematics, String filter) {
        if (filter == null) return schematics;

        Pattern pattern;
        try {
            pattern = buildRegex(filter);
        } catch (PatternSyntaxException e) {
            return null;
        }

        return schematics.stream().filter(schem -> schem.isSchematic(pattern)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * If a directory matches the full name, all schematics inside this directory will be returned directly.
     *
     * @return all schematics inside the directory
     */
    @Override
    public Set<Schematic> getSchematicsByDirectory(Player player, String name, String filter) {
        // if folder name ends with a '*' perform a deep search and return every schematic in folder and sub folders.
        if (name.endsWith("*")) {
            var pureName = name.replace("*", "").toLowerCase();
            Set<Schematic> allSchematics = new LinkedHashSet<>();
            // Check if a directory with this name exists if a directory match should be checked.
            for (var entry : schematicsCache.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(pureName)) {
                    // only the schematics in directory will be returned if a directory is found.
                    allSchematics.addAll(entry.getValue());
                }
            }
            if (userCache.containsKey(player.getUniqueId())) {
                for (var entry : userCache.get(player.getUniqueId()).entrySet()) {
                    if (entry.getKey().toLowerCase().startsWith(pureName)) {
                        // only the schematics in directory will be returned if a directory is found.
                        allSchematics.addAll(entry.getValue());
                    }
                }
            }
            return filterSchematics(allSchematics, filter);
        }
        // Check if a directory with this name exists if a directory match should be checked.
        for (var entry : schematicsCache.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                // only the schematics in directory will be returned if a directory is found.
                return filterSchematics(entry.getValue(), filter);
            }
        }
        if (userCache.containsKey(player.getUniqueId())) {
            for (var entry : userCache.get(player.getUniqueId()).entrySet()) {
                if (name.equalsIgnoreCase(entry.getKey())) {
                    // only the schematics in directory will be returned if a directory is found.
                    return filterSchematics(entry.getValue(), filter);
                }
            }
        }
        return Collections.emptySet();
    }

    /**
     * Get all cached schematics.
     *
     * @return list of schematics
     */
    private Set<Schematic> getSchematics(Player player) {
        Set<Schematic> schematics = new HashSet<>();
        schematicsCache.values().forEach(schematics::addAll);
        if (userCache.containsKey(player.getUniqueId())) {
            userCache.get(player.getUniqueId()).values().forEach(schematics::addAll);
        }
        return schematics;
    }

    /**
     * Convert a string to a regex.
     *
     * @param name name to convert
     * @return name as regex
     * @throws PatternSyntaxException if the string could not be parsed
     */
    private Pattern buildRegex(String name) throws PatternSyntaxException {
        // Check if the name starts with a regex marker
        if (name.startsWith("^")) return Pattern.compile(name);

        // Replace wildcard with greedy regex wildcard and escape regex and other illegal pattern.

        var regex = name
                .replace(".schematic", "")
                .replace(".", "\\.")
                .replace("\\", "")
                .replace("+", "\\+")
                .replace("*", ".*?");

        return Pattern.compile(regex);
    }

    /**
     * Returns a list of matching directories.
     *
     * @param dir   string for lookup
     * @param count amount of returned directories
     * @return list of directory names with size of count or shorter
     */
    @Override
    public List<String> getMatchingDirectories(Player player, String dir, int count) {
        Set<String> matches = new HashSet<>();
        var separator = configuration.schematicConfig().pathSeparator().charAt(0);
        var deep = TextUtil.countChars(dir, separator);
        for (var key : schematicsCache.keySet()) {
            if (key.toLowerCase().startsWith(dir.toLowerCase()) || dir.isEmpty()) {
                matches.add(trimPath(key, separator, deep));
                if (matches.size() > count) break;
            }
        }
        if (userCache.containsKey(player.getUniqueId())) {
            for (var key : userCache.get(player.getUniqueId()).keySet()) {
                if (key.toLowerCase().startsWith(dir.toLowerCase()) || dir.isEmpty()) {
                    matches.add(trimPath(key, separator, deep));
                    if (matches.size() > count) break;
                }
            }
        }
        return new ArrayList<>(matches);
    }

    private String trimPath(String input, char separator, int deep) {
        var count = deep;
        for (var i = 0; i < input.length(); i++) {
            if (input.charAt(i) != separator) continue;
            count--;
            if (count != -1) continue;
            return input.substring(0, i + 1);
        }
        return input;
    }

    /**
     * Returns a list of matching schematics.
     *
     * @param name  string for lookup
     * @param count amount of returned schematics
     * @return list of schematics names with size of count or shorter
     */
    @Override
    public List<String> getMatchingSchematics(Player player, String name, int count) {
        List<String> matches = new ArrayList<>();
        for (var entry : schematicsCache.entrySet()) {
            for (var schematic : entry.getValue()) {
                if (schematic.name().toLowerCase().startsWith(name.toLowerCase())) {
                    matches.add(schematic.name());
                    if (matches.size() > count) break;

                }
            }
        }
        if (userCache.containsKey(player.getUniqueId())) {
            for (var entry : userCache.get(player.getUniqueId()).entrySet()) {
                for (var schematic : entry.getValue()) {
                    if (schematic.name().toLowerCase().startsWith(name.toLowerCase())) {
                        matches.add(schematic.name());
                        if (matches.size() > count) break;

                    }
                }
            }
        }
        return matches;
    }

    @Override
    public int schematicCount() {
        return schematicsCache.values().stream().map(Set::size).mapToInt(Integer::intValue).sum();
    }

    @Override
    public int directoryCount() {
        return schematicsCache.keySet().size();
    }

    @Override
    public void shutdown() {
        watchService.shutdown();
    }

    private record DirectoryData(List<Path> directories, List<File> files) {

    }
}
