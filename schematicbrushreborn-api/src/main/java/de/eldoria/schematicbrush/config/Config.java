package de.eldoria.schematicbrush.config;

import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.config.sections.SchematicConfig;
import de.eldoria.schematicbrush.config.sections.presets.PresetRegistry;

public interface Config {
    void saveConfigs();

    void reloadConfigs();

    SchematicConfig schematicConfig();

    GeneralConfig general();

    PresetRegistry presets();
}
