package de.eldoria.schematicbrush.brush.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class ModifierProvider extends SettingProvider<Mutator> {
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }
}
