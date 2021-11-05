package de.eldoria.schematicbrush.brush.config.provider;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Represents a modifier provider to provide {@link Mutator}.
 */
public abstract class ModifierProvider extends SettingProvider<Mutator<?>> {
    /**
     * Default constructor
     *
     * @param clazz class which is provided
     * @param name  name of provider
     */
    public ModifierProvider(Class<? extends ConfigurationSerializable> clazz, String name) {
        super(clazz, name);
    }
}
