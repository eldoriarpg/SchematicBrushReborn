package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.util.ComponentProvider;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Set;

public interface Selector extends ConfigurationSerializable, ComponentProvider {
    Set<Schematic> select(Player player, SchematicCache cache);

    String name();
}
