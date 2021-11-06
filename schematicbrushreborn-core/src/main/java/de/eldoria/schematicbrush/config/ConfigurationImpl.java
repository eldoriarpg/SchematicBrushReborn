/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.GeneralConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfigImpl;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import de.eldoria.schematicbrush.config.sections.SchematicSourceImpl;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistry;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistryImpl;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConfigurationImpl extends EldoConfig implements Configuration {
    private static final String PRESET_FILE = "presets";
    private SchematicConfig schematicConfig;
    private GeneralConfig general;
    private PresetRegistry presets;

    public ConfigurationImpl(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void saveConfigs() {
        getConfig().set("schematicConfig", schematicConfig);
        loadConfig(PRESET_FILE, null, false).set("presets", presets);
        getConfig().set("general", general);
    }

    @Override
    public void reloadConfigs() {
        presets = loadConfig(PRESET_FILE, null, false).getObject("presets", PresetRegistry.class, new PresetRegistryImpl());
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
                    sources.add(new SchematicSourceImpl(path, prefix, excluded));
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
    public PresetRegistry presets() {
        return presets;
    }
}
