package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.util.TextUtil;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchematicCache implements Runnable {
    private Map<String, List<Schematic>> schematicsCache = new HashMap<>();

    private final Pattern uuid = Pattern.compile("[a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}");
    private final Logger logger = SchematicBrushReborn.logger();
    private final JavaPlugin plugin;

    public SchematicCache(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public void init() {
        Executors
                .newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this, 60, 60, TimeUnit.SECONDS);
        reload();
    }

    /**
     * Reload the current loaded schematics.
     * This overrides the cache, when the schematics are loaded.
     */
    public void reload() {
        if (SchematicBrushReborn.debugMode()) {
            plugin.getLogger().info("Reloading schematics.");
        }
        Map<String, List<Schematic>> cache = new HashMap<>();

        String root = plugin.getDataFolder().toPath().getParent().toString();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("schematicSources.scanPathes");

        if (section == null) {
            // This should never happen.
            plugin.getLogger().warning("schematicSources.scanPathes is missing in config.");
            return;
        }

        boolean prefixActive = plugin.getConfig().getBoolean("selectorSettings.pathSourceAsPrefix");
        String seperator = plugin.getConfig().getString("selectorSettings.pathSeperator");
        List<String> excludedPaths = plugin.getConfig().getStringList("schematicSources.excludedPathes");

        for (String key : section.getKeys(false)) {
            String path = section.getString(key + ".path");
            if (path == null || path.isEmpty()) {
                if (SchematicBrushReborn.debugMode()) {
                    logger.warning("Path " + key + " has no path. Skipping!");
                }
                continue;
            }
            String prefix = section.getString(key + ".prefix");
            if (prefix == null || prefix.isEmpty()) {
                logger.warning("Path " + key + " has no prefix. Skipping!");
                continue;
            }

            path = path.replace("\\", "/");

            loadSchematics(cache, Paths.get(root, path), seperator, prefix, prefixActive, excludedPaths);
        }

        int sum = schematicsCache.values().stream().mapToInt(List::size).sum();
        if (SchematicBrushReborn.debugMode()) {
            logger.info("Loaded " + sum + " schematics from " + schematicsCache.size() + " directories.");
        }
        schematicsCache = cache;
    }

    private void loadSchematics(Map<String, List<Schematic>> cache, Path schematicFolder, String seperator,
                                String prefix, boolean prefixActive, List<String> excludedPaths) {
        // fail silently if this folder does not exist.
        if (!schematicFolder.toFile().exists()) return;

        Optional<DirectoryData> baseDirectoryData = getDirectoryData(schematicFolder);

        if (!baseDirectoryData.isPresent()) {
            logger.warning("Could not load schematics from " + schematicFolder.toString() + " folder.");
            return;
        }

        // initialise queue with directory in first layer
        Queue<Path> deepDirectories = new ArrayDeque<>(baseDirectoryData.get().getDirectories());
        deepDirectories.add(schematicFolder.toFile().toPath());

        // iterate over every directory.
        // load files and add new directories if found.
        while (!deepDirectories.isEmpty()) {
            Path path = deepDirectories.poll();

            // remove path to get relative path in schematic folder.
            String rawKey = path.toString().replace(schematicFolder.toString(), "");


            Optional<DirectoryData> directoryData = getDirectoryData(path);
            if (!directoryData.isPresent()) {
                continue;
            }
            // Queue new directories
            deepDirectories.addAll(directoryData.get().getDirectories());

            // check if path is excluded
            if (isExclued(excludedPaths, prefix + rawKey)) {
                if (SchematicBrushReborn.debugMode()) {
                    logger.info("Skipping exluded path " + prefix + rawKey);
                }
                continue;
            }

            // Build schematic references
            List<Schematic> schematics = new ArrayList<>();

            for (File file : directoryData.get().getFiles()) {
                ClipboardFormat format = ClipboardFormats.findByFile(file);

                if (format == null) continue;

                schematics.add(new Schematic(format, file));
            }

            if (schematics.isEmpty()) continue;
            String key;
            if (!rawKey.isEmpty()) {
                key = rawKey.replace(" ", "_").substring(1).replace("\\", seperator);
            } else {
                key = rawKey;
            }
            if (prefixActive) {
                key = prefix + seperator + key;
            }

            cache.computeIfAbsent(key, k -> new ArrayList<>())
                    // add schematics
                    .addAll(schematics);
            if (SchematicBrushReborn.debugMode()) {
                logger.info("Loaded " + schematics.size() + " schematics from " + path.toString() + " as " + key);
            }
        }
        if (SchematicBrushReborn.debugMode()) {
            logger.info("Loaded schematics from " + schematicFolder.toString());
        }
    }

    private boolean isExclued(List<String> excludes, String path) {
        for (String exclude : excludes) {
            if (exclude.endsWith("*")) {
                if (path.startsWith(exclude.substring(0, exclude.length() - 1))) {
                    return true;
                }
            }
            if (exclude.equalsIgnoreCase(path)) {
                return true;
            }
        }
        return false;
    }

    private Optional<DirectoryData> getDirectoryData(Path directory) {
        List<Path> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();

        // Get a list of all files and directories in a directory
        try (Stream<Path> paths = Files.list(directory)) {
            // Check for each file if its a directory or a file.
            for (Path path : paths.collect(Collectors.toList())) {
                if (path.equals(directory)) continue;
                File file = path.toFile();
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
     * @param name name which will be parsed to a regex.
     * @return A brush config builder with assigned schematics.
     */
    public List<Schematic> getSchematicsByName(String name) {
        Pattern pattern;
        try {
            pattern = buildRegex(name);
        } catch (PatternSyntaxException e) {
            return null;
        }

        return getSchematics().stream().filter(c -> c.isSchematic(pattern)).collect(Collectors.toList());
    }

    /**
     * If a directory matches the full name, all schematics inside this directory will be returned directly.
     *
     * @return all schematics inside the directory
     */
    public List<Schematic> getSchematicsByDirectory(String name) {
        // if folder name ends with a '*' perform a deep search and return every schematic in folder and sub folders.
        if (name.endsWith("*")) {
            String purename = name.replace("*", "").toLowerCase();
            List<Schematic> allSchematics = new ArrayList<>();
            // Check if a directory with this name exists if a directory match should be checked.
            for (Map.Entry<String, List<Schematic>> entry : schematicsCache.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(purename)) {
                    // only the schematics in directory will be returned if a directory is found.
                    allSchematics.addAll(entry.getValue());
                }
            }
            return allSchematics;
        } else {
            // Check if a directory with this name exists if a directory match should be checked.
            for (Map.Entry<String, List<Schematic>> entry : schematicsCache.entrySet()) {
                if (name.equalsIgnoreCase(entry.getKey())) {
                    // only the schematics in directory will be returned if a directory is found.
                    return entry.getValue();
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get all cached schematics.
     *
     * @return list of schematics
     */
    private List<Schematic> getSchematics() {
        List<Schematic> schematics = new ArrayList<>();
        schematicsCache.values().forEach(schematics::addAll);
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

        String regex = name
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
    public List<String> getMatchingDirectories(String dir, int count) {
        Set<String> matches = new HashSet<>();
        char seperator = plugin.getConfig().getString("selectorSettings.pathSeperator").charAt(0);
        int deep = TextUtil.countChars(dir, seperator);
        for (String k : schematicsCache.keySet()) {
            if (k.toLowerCase().startsWith(dir.toLowerCase()) || dir.isEmpty()) {
                matches.add(trimPath(k, seperator, deep));
                if (matches.size() > count) break;
            }
        }
        return new ArrayList<>(matches);
    }

    private String trimPath(String string, char seperator, int deep) {
        int count = deep;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != seperator) continue;
            count--;
            if (count != -1) continue;
            return string.substring(0, i + 1);
        }
        return string;
    }

    /**
     * Returns a list of matching schematics.
     *
     * @param name  string for lookup
     * @param count amount of returned schematics
     * @return list of schematics names with size of count or shorter
     */
    public List<String> getMatchingSchematics(String name, int count) {
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, List<Schematic>> entry : schematicsCache.entrySet()) {
            for (Schematic schematic : entry.getValue()) {
                if (schematic.getName().toLowerCase().startsWith(name.toLowerCase())) {
                    matches.add(schematic.getName());
                    if (matches.size() > count) break;

                }
            }
        }
        return matches;
    }

    public int schematicCount() {
        return schematicsCache.values().stream().map(List::size).mapToInt(Integer::intValue).sum();
    }
    public int directoryCount() {
        return schematicsCache.keySet().size();
    }

    @Override
    public void run() {
        reload();
    }

    @Data
    private static class DirectoryData {
        private List<Path> directories;
        private List<File> files;

        public DirectoryData(List<Path> directories, List<File> files) {
            this.directories = directories;
            this.files = files;
        }
    }
}
