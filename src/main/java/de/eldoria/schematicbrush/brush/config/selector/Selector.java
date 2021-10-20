package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Set;

public interface Selector extends ConfigurationSerializable {
    Set<Schematic> select(Player player, SchematicCache cache);
}
