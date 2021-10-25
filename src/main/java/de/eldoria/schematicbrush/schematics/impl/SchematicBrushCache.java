package de.eldoria.schematicbrush.schematics.impl;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import de.eldoria.eldoutilities.utils.TextUtil;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.Nameable;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
    public static Nameable key = Nameable.of("default");
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    private final Logger logger = SchematicBrushReborn.logger();
    private final JavaPlugin plugin;
    private final Config config;
    private final Map<String, Set<Schematic>> schematicsCache = new HashMap<>();
    private final Map<UUID, Map<String, Set<Schematic>>> userCache = new HashMap<>();
    private SchematicWatchService watchService;

    public SchematicBrushCache(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public void init() {
        reload();
        watchService = SchematicWatchService.of(plugin, config, this);
    }

    /**
     * Reload the current loaded schematics. This overrides the cache, when the schematics are loaded.
     */
    @Override
    public void reload() {
        plugin.getLogger().log(Level.CONFIG, "Reloading schematics.");

        var root = plugin.getDataFolder().toPath().getParent().toString();

        schematicsCache.clear();

        for (var key : config.getSchematicConfig().getSources()) {
            var path = key.getPath();
            if (path == null || path.isEmpty()) {
                plugin.getLogger().log(Level.CONFIG, "Path " + key + " has no path. Skipping!");
                continue;
            }

            path = path.replace("\\", "/");

            loadSchematics(Paths.get(root, path));
        }
    }

    private void loadSchematics(Path schematicFolder) {
        // fail silently if this folder does not exist.
        if (!schematicFolder.toFile().exists()) return;

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
                addSchematic(file);
            }
            logger.log(Level.CONFIG, "Loaded schematics from " + path.toString());
        }
        logger.log(Level.CONFIG, "Loaded schematics from " + schematicFolder);
    }

    public void removeSchematic(File file) {
        if (file.isDirectory()) {
            return;
        }

        for (var value : schematicsCache.values()) {
            value.removeIf(schematic -> schematic.getFile() == file);
        }
    }

    public void addSchematic(File file) {
        var directory = file.toPath().getParent();
        directory = directory.subpath(1, directory.getNameCount());

        var sourceForPath = config.getSchematicConfig().getSourceForPath(directory);

        if (sourceForPath.isEmpty()) {
            logger.log(Level.CONFIG, "File " + directory + "is not part of a source");
            return;
        }

        var source = sourceForPath.get();

        if (source.isExcluded(directory)) {
            logger.log(Level.CONFIG, "Directory " + directory + "is exluded.");
            return;
        }

        // remove path to get relative path in schematic folder.
        var rawKey = directory.toString().replace(source.getPath(), "");

        String key;
        if (!rawKey.isEmpty()) {
            key = rawKey.replace(" ", "_").substring(1).replace("\\", config.getSchematicConfig().getPathSeparator());
        } else {
            key = rawKey;
        }

        UUID playerUid = null;

        var matcher = UUID_PATTERN.matcher(rawKey);
        if (matcher.find()) {
            var uuidString = matcher.group();
            logger.log(Level.CONFIG, "Found UUID " + uuidString);
            playerUid = UUID.fromString(uuidString);
            key = key.replaceFirst(uuidString + "/?", "");
        }

        if (config.getSchematicConfig().isPathSourceAsPrefix()) {
            key = source.getPrefix() + config.getSchematicConfig().getPathSeparator() + key;
        }

        var format = ClipboardFormats.findByFile(file);

        if (format == null) {
            logger.log(Level.CONFIG, "Could not determine schematic type of " + file.toPath());
            return;
        }

        logger.log(Level.CONFIG, "Added " + file.toPath() + " to schematic cache.");
        if (playerUid != null) {
            userCache.computeIfAbsent(playerUid, k -> new HashMap<>()).computeIfAbsent(key, k -> new HashSet<>()).add(new Schematic(format, file));
        } else {
            schematicsCache.computeIfAbsent(key, k -> new HashSet<>()).add(new Schematic(format, file));
        }
    }

    private Optional<DirectoryData> getDirectoryData(Path directory) {
        List<Path> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();

        // Get a list of all files and directories in a directory
        try (var paths = Files.list(directory)) {
            // Check for each file if its a directory or a file.
            for (var path : paths.collect(Collectors.toList())) {
                if (path.equals(directory)) continue;
                var file = path.toFile();
                if (file.isDirectory()) {
                    directories.add(path);
                } else if (file.isFile()) {
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

        return schematics.stream().filter(c -> c.isSchematic(pattern)).collect(Collectors.toSet());
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
            var purename = name.replace("*", "").toLowerCase();
            Set<Schematic> allSchematics = new HashSet<>();
            // Check if a directory with this name exists if a directory match should be checked.
            for (var entry : schematicsCache.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(purename)) {
                    // only the schematics in directory will be returned if a directory is found.
                    allSchematics.addAll(entry.getValue());
                }
            }
            if (userCache.containsKey(player.getUniqueId())) {
                for (var entry : userCache.get(player.getUniqueId()).entrySet()) {
                    if (entry.getKey().toLowerCase().startsWith(purename)) {
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
                .replace("*", ".+?");

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
        var seperator = config.getSchematicConfig().getPathSeparator().charAt(0);
        var deep = TextUtil.countChars(dir, seperator);
        for (var k : schematicsCache.keySet()) {
            if (k.toLowerCase().startsWith(dir.toLowerCase()) || dir.isEmpty()) {
                matches.add(trimPath(k, seperator, deep));
                if (matches.size() > count) break;
            }
        }
        if (userCache.containsKey(player.getUniqueId())) {
            for (var k : userCache.get(player.getUniqueId()).keySet()) {
                if (k.toLowerCase().startsWith(dir.toLowerCase()) || dir.isEmpty()) {
                    matches.add(trimPath(k, seperator, deep));
                    if (matches.size() > count) break;
                }
            }
        }
        return new ArrayList<>(matches);
    }

    private String trimPath(String input, char seperator, int deep) {
        var count = deep;
        for (var i = 0; i < input.length(); i++) {
            if (input.charAt(i) != seperator) continue;
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

    public void shutdown() {
        watchService.shutdown();
    }

    private static class DirectoryData {
        private List<Path> directories;
        private List<File> files;

        public DirectoryData(List<Path> directories, List<File> files) {
            this.directories = directories;
            this.files = files;
        }

        public List<Path> directories() {
            return directories;
        }

        public void directories(List<Path> directories) {
            this.directories = directories;
        }

        public List<File> files() {
            return files;
        }

        public void files(List<File> files) {
            this.files = files;
        }
    }
}
