package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.brush.config.Mutator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class ModifierProvider extends SettingProvider<Mutator<?>> {
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }
}
