/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import de.eldoria.eldoutilities.config.ConfigKey;
import de.eldoria.eldoutilities.config.JacksonConfig;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.MainConfiguration;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushes;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresets;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class JacksonConfiguration extends JacksonConfig<MainConfiguration> implements Configuration {
    public static final ConfigKey<YamlPresets> PRESETS = ConfigKey.of("Presets", Path.of("presets.yml"), YamlPresets.class, YamlPresets::new);
    public static final ConfigKey<YamlBrushes> BRUSHES = ConfigKey.of("Brushes", Path.of("brushes.yml"), YamlBrushes.class, YamlBrushes::new);

    /**
     * Creates a new Jackson Configuration
     *
     * @param plugin plugin owning the configuration
     */
    public JacksonConfiguration(@NotNull SchematicBrushReborn plugin) {
        super(plugin, ConfigKey.defaultConfig(MainConfiguration.class, MainConfiguration::new));
    }

    @Override
    public void saveConfigs() {
        save();
    }

    @Override
    public void reloadConfigs() {
        reload();
    }

    @Override
    public SchematicConfig schematicConfig() {
        return main().schematicConfig();
    }

    @Override
    public GeneralConfig general() {
        return main().generalConfig();
    }

    @Override
    protected ObjectMapper createMapper() {
        return ((SchematicBrushReborn) plugin()).configureMapper(YAMLMapper.builder());
    }

    @Override
    public Presets presets() {
        return secondary(PRESETS);
    }

    @Override
    public Brushes brushes() {
        return secondary(BRUSHES);
    }
}
