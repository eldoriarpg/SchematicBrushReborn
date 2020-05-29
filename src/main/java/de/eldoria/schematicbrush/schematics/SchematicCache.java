package de.eldoria.schematicbrush.schematics;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class SchematicCache {
    private final Map<String, List<Schematic>> schematicsCache = new HashMap<>();

    private final Pattern uuid = Pattern.compile("[a-zA-Z0-9]{8}(-[a-zA-Z0-9]{4}){3}-[a-zA-Z0-9]{12}");
    private final Logger logger = SchematicBrushReborn.logger();
    private final JavaPlugin plugin;

    public SchematicCache(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    public void init() {
        reload();
    }

    public void reload() {
        schematicsCache.clear();

        // Check if internal schematics directory exists
        Path internalSchematics = Path.of(plugin.getDataFolder().getPath(), "schematics");
        File schematicsDirectory = internalSchematics.toFile();
        if (!schematicsDirectory.exists()) {
            boolean success = schematicsDirectory.mkdir();
            if (!success) {
                logger.warning("Could not create schematics ordner.");
            }
        }

        // Load schematics of schematic brush, FAWE and vanilla world edit.
        loadSchematics(internalSchematics);
        loadSchematics(Path.of(plugin.getDataFolder().toPath().getParent().toString(), "WorldEdit", "schematics"));
        loadSchematics(Path.of(plugin.getDataFolder().toPath().getParent().toString(), "FastAsyncWorldEdit", "schematics"));

        int sum = schematicsCache.values().stream().mapToInt(List::size).sum();
        logger.info("Loaded " + sum + " schematics from " + schematicsCache.size() + " directories.");
    }

    private void loadSchematics(Path schematicFolder) {
        if (!schematicFolder.toFile().exists()) return;

        List<Path> directories;
        try (var list = Files.list(schematicFolder)) {
            directories = list
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.warning("Could not load schematics from \"schematics folder in schematic brush folder.");
            return;
        }

        for (var dir : directories) {
            List<Schematic> schematics = new ArrayList<>();
            try (var files = Files.list(dir)) {
                for (Path path : files.collect(Collectors.toList())) {
                    File file = path.toFile();
                    if (!file.isFile()) continue;

                    ClipboardFormat format = ClipboardFormats.findByFile(file);

                    if (format == null) continue;

                    schematics.add(new Schematic(format, file));
                }
            } catch (IOException e) {
                continue;
            }

            schematicsCache.computeIfAbsent(dir.getFileName().toString(), k -> new ArrayList<>()).addAll(schematics);
        }
        logger.info("Loaded schematics from " + schematicFolder.toString());
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
     * @return
     */
    public List<Schematic> getSchematicsByDirectory(String name) {
        // Check if a directory with this name exists if a directory match should be checked.
        for (Map.Entry<String, List<Schematic>> entry : schematicsCache.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                // only the schematics in directory will be returned if a directory is found.
                return entry.getValue();
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
        List<String> matches = new ArrayList<>();
        for (String k : schematicsCache.keySet()) {
            if (k.toLowerCase().startsWith(dir.toLowerCase())) {
                matches.add(k);
                if (matches.size() > count) break;
            }
        }
        return matches;
    }

    /**
     * Returns a list of matching schematics.
     *
     * @param name   string for lookup
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
}
