package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class RegexSelector extends BaseSelector {
    public RegexSelector(String term) {
        super("^" + term);
    }

    public RegexSelector(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public Set<Schematic> select(Player player, SchematicCache cache) {
        return cache.getSchematicsByName(player, term());
    }
}
