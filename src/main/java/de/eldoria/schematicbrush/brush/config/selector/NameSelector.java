package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class NameSelector extends BaseSelector {


    public NameSelector(String term) {
        super(term);
    }

    public NameSelector(Map<String, Object> objectMap) {
        super(SerializationUtil.mapOf(objectMap).getValue("term"));
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }


    @Override
    public Set<Schematic> select(Player player, SchematicCache cache) {
        return cache.getSchematicsByName(player, term());
    }
}
