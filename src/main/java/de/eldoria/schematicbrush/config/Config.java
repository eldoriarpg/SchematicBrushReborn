package de.eldoria.schematicbrush.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistry;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Config extends EldoConfig {
    private SchematicConfig schematicConfig;
    private GeneralConfig general;
    private PresetRegistry presets;

    public Config(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void saveConfigs() {
        getConfig().set("schematicConfig", schematicConfig);
        getConfig().set("presets", presets);
        getConfig().set("general", general);
    }

    @Override
    protected void reloadConfigs() {
        schematicConfig = getConfig().getObject("schematicConfig", SchematicConfig.class, new SchematicConfig());
        general = getConfig().getObject("general", GeneralConfig.class, new GeneralConfig());
        presets = getConfig().getObject("presets", PresetRegistry.class, new PresetRegistry());
    }

    @Override
    protected void init() {
        var version = getConfig().getInt("version", -1);
        if (version == -1) {
            setVersion(3, true);
            return;
        }

        if (version == 2) {
            upgradeToV3();
        }

        if (version == 3) {
            migrateToV4();
        }
    }

    private void migrateToV4() {
        plugin.getLogger().info("Converting config to Version 4");
        getConfig().set("presets", null);
        setVersion(4, true);
    }

    private void upgradeToV3() {
        plugin.getLogger().info("Converting config to Version 3");

        plugin.getLogger().info("Creating backup of config.");

        try {
            var path = Paths.get(plugin.getDataFolder().toPath().toString(), "config_old.yml");
            if (!path.toFile().exists()) {
                Files.createFile(path);
            }
            Files.write(Paths.get(plugin.getDataFolder().toPath().toString(), "config_old.yml"), getConfig().saveToString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create backup. Converting aborted", e);
        }

        List<SchematicSource> sources = new ArrayList<>();
        var schematicSources = getConfig().getConfigurationSection("schematicSources");

        if (schematicSources != null) {
            var excludedPathes = schematicSources.getStringList("excludedPathes");

            var scanPath = schematicSources.getConfigurationSection("scanPathes");
            if (scanPath != null) {
                for (var key : scanPath.getKeys(false)) {
                    var path = scanPath.getString(key + ".path");
                    plugin.getLogger().info("Converting path " + path);
                    var prefix = scanPath.getString(key + ".prefix", "null");
                    List<String> excluded = new ArrayList<>();
                    for (var currpath : excludedPathes) {
                        if (currpath.startsWith(prefix)) {
                            plugin.getLogger().info("Found exclusion in path for directory " + currpath);
                            excluded.add(currpath.replace(prefix + "/", ""));
                        }
                    }
                    sources.add(new SchematicSource(path, prefix, excluded));
                    plugin.getLogger().info("Source " + path + " successfully converted.");
                }
            }
        }
        getConfig().set("schematicSources", null);
        plugin.getLogger().info("Converted schematic sources and deleted.");

        var selectorSettings = getConfig().getConfigurationSection("selectorSettings");
        var pathSeperator = "/";
        var pathSourceAsPrefix = false;
        if (selectorSettings != null) {
            pathSeperator = selectorSettings.getString("pathSeperator", "/");
            pathSourceAsPrefix = selectorSettings.getBoolean("pathSourceAsPrefix", false);
        }

        plugin.getLogger().info("Converted selector setting and deleted.");
        getConfig().set("selectorSettings", null);

        getConfig().set("schematicConfig", new SchematicConfig(sources, pathSeperator, pathSourceAsPrefix));

        getConfig().set("presets", null);

        setVersion(3, false);
    }

    public SchematicConfig schematicConfig() {
        return schematicConfig;
    }

    public GeneralConfig general() {
        return general;
    }

    public PresetRegistry presets() {
        return presets;
    }
}
