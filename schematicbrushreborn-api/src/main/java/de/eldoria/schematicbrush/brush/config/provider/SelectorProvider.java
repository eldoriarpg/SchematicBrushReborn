package de.eldoria.schematicbrush.brush.config.provider;

import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class SelectorProvider extends SettingProvider<Selector> {

    private final SchematicRegistry registry;

    public SelectorProvider(Class<? extends ConfigurationSerializable> clazz, String name, SchematicRegistry registry) {
        super(clazz, name);
        this.registry = registry;
    }

    public SchematicRegistry registry() {
        return registry;
    }
}
