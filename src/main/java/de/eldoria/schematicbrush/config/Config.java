package de.eldoria.schematicbrush.config;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.Preset;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Config extends EldoConfig {
    private SchematicConfig schematicConfig;
    private Map<String, Preset> presets;
    private GeneralConfig general;

    public Config(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void saveConfigs() {
        getConfig().set("schematicConfig", schematicConfig);
        getConfig().set("presets", new ArrayList<>(presets.values()));
        getConfig().set("general", general);
    }

    @Override
    protected void reloadConfigs() {
        schematicConfig = getConfig().getObject("schematicConfig", SchematicConfig.class, new SchematicConfig());
        general = getConfig().getObject("general", GeneralConfig.class, new GeneralConfig());
        presets = new HashMap<>();
        List<Preset> presets = (List<Preset>) getConfig().getList("presets", new ArrayList<>());
        for (Preset preset : presets) {
            this.presets.put(preset.getName().toLowerCase(Locale.ROOT), preset);
        }
    }

    @Override
    protected void init() {
        int version = plugin.getConfig().getInt("version", -1);
        if (version == 2) {
            upgradeToV3();
        }
    }

    private void upgradeToV3() {
        plugin.getLogger().info("Converting config to Version 3");

        plugin.getLogger().info("Creating backup of config.");

        try {
            Path path = Paths.get(plugin.getDataFolder().toPath().toString(), "config_old.yml");
            if (!path.toFile().exists()) {
                path.toFile().createNewFile();
            }
            Files.write(Paths.get(plugin.getDataFolder().toPath().toString(), "config_old.yml"), plugin.getConfig().saveToString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create backup. Converting aborted", e);
        }

        List<SchematicSource> sources = new ArrayList<>();
        ConfigurationSection schematicSources = plugin.getConfig().getConfigurationSection("schematicSources");

        if (schematicSources != null) {
            List<String> excludedPathes = schematicSources.getStringList("excludedPathes");

            ConfigurationSection scanPath = schematicSources.getConfigurationSection("scanPathes");
            if (scanPath != null) {
                for (String key : scanPath.getKeys(false)) {
                    String path = scanPath.getString(key + ".path");
                    plugin.getLogger().info("Converting path " + path);
                    String prefix = scanPath.getString(key + ".prefix");
                    List<String> excluded = new ArrayList<>();
                    for (String currpath : excludedPathes) {
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
        plugin.getConfig().set("schematicSources", null);
        plugin.getLogger().info("Converted schematic sources and deleted.");

        ConfigurationSection selectorSettings = plugin.getConfig().getConfigurationSection("selectorSettings");
        String pathSeperator = "/";
        boolean pathSourceAsPrefix = false;
        if (selectorSettings != null) {
            pathSeperator = selectorSettings.getString("pathSeperator", "/");
            pathSourceAsPrefix = selectorSettings.getBoolean("pathSourceAsPrefix", false);
        }

        plugin.getLogger().info("Converted selector setting and deleted.");
        plugin.getConfig().set("selectorSettings", null);

        plugin.getConfig().set("schematicConfig", new SchematicConfig(sources, pathSeperator, pathSourceAsPrefix));

        ConfigurationSection presetSection = plugin.getConfig().getConfigurationSection("presets");
        List<Preset> presets = new ArrayList<>();
        if (presetSection != null) {
            for (String key : presetSection.getKeys(false)) {
                List<String> filter = presetSection.getStringList(key + ".filter");
                String description = presetSection.getString(key + ".description");
                presets.add(new Preset(key, description, filter));
                plugin.getLogger().info("Converted preset " + key);
            }
        }

        plugin.getLogger().info("Converted presets.");
        plugin.getConfig().set("presets", presets);
        plugin.getConfig().set("version", 3);
        plugin.saveConfig();
    }

    public boolean presetExists(String name) {
        return getPreset(name).isPresent();
    }

    public Optional<Preset> getPreset(String name) {
        return Optional.ofNullable(presets.get(name.toLowerCase(Locale.ROOT)));
    }

    public void addPreset(Preset preset) {
        presets.put(preset.getName().toLowerCase(Locale.ROOT), preset);
    }

    public boolean removePreset(String name) {
        return presets.remove(name.toLowerCase(Locale.ROOT)) != null;
    }

    public Collection<Preset> getPresets() {
        return presets.values();
    }

    public List<String> getPresetName() {
        return presets.values().stream().map(Preset::getName).collect(Collectors.toList());
    }

    public SchematicConfig getSchematicConfig() {
        return schematicConfig;
    }

    public GeneralConfig getGeneral() {
        return general;
    }
}
