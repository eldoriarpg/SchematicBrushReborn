package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.entity.Player;

import java.util.Set;

public interface Selector {
    Set<Schematic> select(Player player);
}
