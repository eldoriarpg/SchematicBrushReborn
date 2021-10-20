package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;

import java.util.Set;

public class RegexSelector extends BaseSelector {
    public RegexSelector(String term, SchematicCache cache) {
        super("^" + term, cache);
    }

    @Override
    public Set<Schematic> select(Player player) {
        return cache().getSchematicsByName(player, term());
    }
}
