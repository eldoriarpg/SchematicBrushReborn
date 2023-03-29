/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.GeneralConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import de.eldoria.schematicbrush.config.sections.SchematicSourceImpl;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushes;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresets;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LegacyConfiguration extends EldoConfig implements Configuration {
    private static final String PRESET_FILE = "presets";
    private static final String BRUSH_FILE = "brushes";
    private SchematicConfig schematicConfig;
    private GeneralConfig general;
    private Presets presets;
    private Brushes brushes;

    public LegacyConfiguration(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void saveConfigs() {
        getConfig().set("schematicConfig", schematicConfig);
        loadConfig(PRESET_FILE, null, false).set("presets", presets);
        loadConfig(BRUSH_FILE, null, false).set("brushes", brushes);
        getConfig().set("general", general);
    }

    @Override
    public void reloadConfigs() {
        presets = loadConfig(PRESET_FILE, null, false).getObject("presets", Presets.class, new YamlPresets());
        brushes = loadConfig(BRUSH_FILE, null, false).getObject("brushes", Brushes.class, new YamlBrushes());
        schematicConfig = getConfig().getObject("schematicConfig", SchematicConfig.class, new SchematicConfigImpl());
        general = getConfig().getObject("general", GeneralConfig.class, new GeneralConfigImpl());
    }

    @Override
    protected void init() {
        var version = getConfig().getInt("version", -1);
        if (version == -1) {
            setVersion(3, true);
            return;
        }

        if (version < 2) {
            // v1 config does not really contain important data anyway...
            getConfig().getKeys(false).forEach(key -> getConfig().set(key, null));
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
        plugin.getConfig().set("presets", null);
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
            Files.writeString(Paths.get(plugin.getDataFolder().toPath().toString(), "config_old.yml"),
                    getConfig().saveToString(), StandardOpenOption.TRUNCATE_EXISTING);
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
                    sources.add(new SchematicSourceImpl(path, prefix, true, excluded));
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

        getConfig().set("schematicConfig", new SchematicConfigImpl(sources, pathSeperator, pathSourceAsPrefix));

        getConfig().set("presets", null);

        setVersion(3, false);
    }

    @Override
    public SchematicConfig schematicConfig() {
        return schematicConfig;
    }

    @Override
    public GeneralConfig general() {
        return general;
    }

    @Override
    public Presets presets() {
        return presets;
    }

    @Override
    public Brushes brushes() {
        return brushes;
    }
}
