package de.eldoria.schematicbrush;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public final class ConfigUpdater {
    private ConfigUpdater() {
    }

    public static void validateConfig(Plugin plugin) {
        int version = plugin.getConfig().getInt("version");

        switch (version) {
            case 1:
                updateToVersion2(plugin);
                plugin.getLogger().info("Config updated to version 2!");
            case 2:
                // Optional v3 update
                break;
            default:
                plugin.getLogger().warning("Config version is invalid. Config restore performed.");
                plugin.getConfig().set("version", 1);
                validateConfig(plugin);
        }
        ensureConfigConsistency(plugin);
        plugin.saveConfig();
    }

    private static void updateToVersion2(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        config.set("version", 2);

        setIfAbsent(config, "debug", false);
        setIfAbsent(config, "metrics", true);

        setIfAbsent(config, "selectorSettings.pathSeperator", "/");

        setIfAbsent(config, "selectorSettings.pathSourceAsPrefix", false);


        // initialise default sources
        ConfigurationSection sources = createSectionIfAbsent(config, "schematicSources.scanPathes");
        setIfAbsent(sources, "schematicBrush.path", "SchematicBrushReborn\\schematics");
        setIfAbsent(sources, "schematicBrush.prefix", "sbr");
        setIfAbsent(sources, "fawe.path", "FastAsyncWorldEdit\\schematics");
        setIfAbsent(sources, "fawe.prefix", "fawe");
        setIfAbsent(sources, "worldEdit.path", "WorldEdit\\schematics");
        setIfAbsent(sources, "worldEdit.prefix", "we");

        // initialise exluded path entry
        setIfAbsent(config, "schematicSources.excludedPathes", Collections.singletonList("none"));
    }

    private static void ensureConfigConsistency(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();
        setIfAbsent(config, "debug", false);
        ConfigurationSection sources = createSectionIfAbsent(config, "schematicSources.scanPathes");
        setIfAbsent(config, "selectorSettings.pathSeperator", "/");
        String o = plugin.getConfig().getString("selectorSettings.pathSeperator");
        if (o.length() != 1) {
            plugin.getLogger().warning("Path seperator invalid. Must be only one char.");
            plugin.getConfig().set("selectorSettings.pathSeperator", "/");
        }
        setIfAbsent(config, "selectorSettings.pathSourceAsPrefix", false);
        setIfAbsent(config, "schematicSources.excludedPathes", Collections.singletonList("none"));
    }

    private static void setIfAbsent(FileConfiguration config, String path, Object value) {
        if (!config.isSet(path)) {
            config.set(path, value);
        }
    }

    private static void setIfAbsent(ConfigurationSection section, String path, Object value) {
        if (!section.isSet(path)) {
            section.set(path, value);
        }
    }

    private static ConfigurationSection createSectionIfAbsent(FileConfiguration config, String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return config.createSection(path);
        }
        return section;
    }
}
