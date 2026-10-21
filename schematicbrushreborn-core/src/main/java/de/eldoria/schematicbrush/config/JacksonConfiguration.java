/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.config;

import de.eldoria.eldoutilities.config.JacksonConfig;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.MainConfiguration;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.brushes.YamlBrushes;
import de.eldoria.schematicbrush.config.sections.presets.YamlPresets;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;
import dev.chojo.ocular.key.Key;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.JacksonModule;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JacksonConfiguration extends JacksonConfig<MainConfiguration> implements Configuration {
    public static final Key<YamlPresets> PRESETS = Key.builder(Path.of("presets.yml"), YamlPresets::new).name("Presets").build();
    public static final Key<YamlBrushes> BRUSHES = Key.builder(Path.of("brushes.yml"), YamlBrushes::new).name("Brushes").build();

    /**
     * Creates a new Jackson Configuration
     *
     * @param plugin plugin owning the configuration
     */
    public JacksonConfiguration(@NotNull SchematicBrushReborn plugin) {
        super(plugin, Key.builder(Path.of("config.yml"), MainConfiguration::new).build());
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
    public List<JacksonModule> additionalModules() {
        var modules = new ArrayList<>(super.additionalModules());
        modules.add(((SchematicBrushReborn) plugin()).schematicBrushModule());
        return modules;
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
