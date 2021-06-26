package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import de.eldoria.eldoutilities.utils.TextUtil;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.config.Config;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class SchematicCache implements Runnable {
    private final Pattern uuid = Pattern.compile("[a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}");
    private final Logger logger = SchematicBrushReborn.logger();
    private final JavaPlugin plugin;
    private final Config config;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    WatchService watchService;
    private final Map<String, Set<Schematic>> schematicsCache = new HashMap<>();
    private Thread watchThread;

    public SchematicCache(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void init() {
        reload();
        initWatchServices();
    }

    public void initWatchServices() {

        String root = plugin.getDataFolder().toPath().getParent().toString();

        List<SchematicSource> sources = config.getSchematicConfig().getSources();
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            logger.log(Level.CONFIG, "Could not create watch service");
            return;
        }

        for (SchematicSource source : sources) {
            Path path = Paths.get(root, source.getPath());
            watchDirectory(watchService, path);
        }

        watchThread = new Thread(() -> {
            Thread.currentThread().setName("Schematic Brush Watch Service.");
            while (true) {
                WatchKey key;
                key = watchService.poll();
                if (key == null) continue;
                plugin.getLogger().log(Level.CONFIG, "Detected change in file system.");
                for (WatchEvent<?> event : key.pollEvents()) {
                    File path = ((Path) key.watchable()).resolve(event.context().toString()).toFile();
                    switch (event.kind().name()) {
                        case "ENTRY_CREATE":
                            plugin.getLogger().log(Level.CONFIG, "A new schematic was detected. Trying to add.");
                            executorService.schedule(() -> addSchematic(path), 5, TimeUnit.SECONDS);
                            break;
                        case "ENTRY_DELETE":
                            plugin.getLogger().log(Level.CONFIG, "A schematic was deleted. Trying to remove.");
                            executorService.schedule(() -> removeSchematic(path), 5, TimeUnit.SECONDS);
                            break;
                    }
                }
                key.reset();
            }
        });
        watchThread.start();
    }

    private void watchDirectory(WatchService watcher, Path path) {
        if (!path.toFile().exists()) {
            logger.info("Path: " + path + " does not exists. Skipping watch service registration.");
            return;
        }
        try {
            // register directory and subdirectories
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException {
                    dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
                    logger.log(Level.CONFIG, "Registered watch service on: " + dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not register watch service.", e);
        }
    }

    /**
     * Reload the current loaded schematics. This overrides the cache, when the schematics are loaded.
     */
    public void reload() {
        plugin.getLogger().log(Level.CONFIG, "Reloading schematics.");

        String root = plugin.getDataFolder().toPath().getParent().toString();

        schematicsCache.clear();

        for (SchematicSource key : config.getSchematicConfig().getSources()) {
            String path = key.getPath();
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

        Optional<DirectoryData> baseDirectoryData = getDirectoryData(schematicFolder);

        if (!baseDirectoryData.isPresent()) {
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
            Path path = deepDirectories.poll();

            Optional<DirectoryData> directoryData = getDirectoryData(path);
            if (!directoryData.isPresent()) {
                continue;
            }
            // Queue new directories
            deepDirectories.addAll(directoryData.get().directories());

            // Build schematic references
            for (File file : directoryData.get().files()) {
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

        for (Set<Schematic> value : schematicsCache.values()) {
            File remove = null;
            for (Schematic schematic : value) {
                // laziest implementation ever...
                if (schematic.getFile() == file) {
                    remove = file;
                    break;
                }
            }
            if (remove != null) {
                File finalRemove = remove;
                value.removeIf(schematic -> schematic.getFile() == finalRemove);
            }
        }
    }

    private void addSchematic(File file) {
        if (file.isDirectory()) {
            watchDirectory(watchService, file.toPath());
            return;
        }

        Path directory = file.toPath().getParent();
        directory = directory.subpath(1, directory.getNameCount());


        Optional<SchematicSource> sourceForPath = config.getSchematicConfig().getSourceForPath(directory);

        if (!sourceForPath.isPresent()) {
            logger.log(Level.CONFIG, "File " + directory + "is not part of a source");
            return;
        }

        SchematicSource source = sourceForPath.get();

        if (source.isExcluded(directory)) {
            logger.log(Level.CONFIG, "Directory " + directory + "is exluded.");
            return;
        }

        // remove path to get relative path in schematic folder.
        String rawKey = directory.toString().replace(source.getPath(), "");

        String key;
        if (!rawKey.isEmpty()) {
            key = rawKey.replace(" ", "_").substring(1).replace("\\", config.getSchematicConfig().getPathSeparator());
        } else {
            key = rawKey;
        }

        if (config.getSchematicConfig().isPathSourceAsPrefix()) {
            key = source.getPrefix() + config.getSchematicConfig().getPathSeparator() + key;
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);

        if (format == null) {
            logger.log(Level.CONFIG, "Could not determine schematic type of " + file.toPath());
            return;
        }

        logger.log(Level.CONFIG, "Added " + file.toPath() + " to schematic cache.");
        schematicsCache.computeIfAbsent(key, k -> new HashSet<>()).add(new Schematic(format, file));
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
    public Set<Schematic> getSchematicsByName(String name) {
        return filterSchematics(getSchematics(), name);
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
    public Set<Schematic> getSchematicsByDirectory(String name, String filter) {
        // if folder name ends with a '*' perform a deep search and return every schematic in folder and sub folders.
        if (name.endsWith("*")) {
            String purename = name.replace("*", "").toLowerCase();
            Set<Schematic> allSchematics = new HashSet<>();
            // Check if a directory with this name exists if a directory match should be checked.
            for (Map.Entry<String, Set<Schematic>> entry : schematicsCache.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(purename)) {
                    // only the schematics in directory will be returned if a directory is found.
                    allSchematics.addAll(entry.getValue());
                }
            }
            return filterSchematics(allSchematics, filter);
        } else {
            // Check if a directory with this name exists if a directory match should be checked.
            for (Map.Entry<String, Set<Schematic>> entry : schematicsCache.entrySet()) {
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
    private Set<Schematic> getSchematics() {
        Set<Schematic> schematics = new HashSet<>();
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
        char seperator = config.getSchematicConfig().getPathSeparator().charAt(0);
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
        for (Map.Entry<String, Set<Schematic>> entry : schematicsCache.entrySet()) {
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
        return schematicsCache.values().stream().map(Set::size).mapToInt(Integer::intValue).sum();
    }

    public int directoryCount() {
        return schematicsCache.keySet().size();
    }

    @Override
    public void run() {
        reload();
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
