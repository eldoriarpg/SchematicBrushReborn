package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.impl.SchematicBrushCache;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class NameSelector extends BaseSelector {


    public NameSelector(String term) {
        super(term);
    }

    public NameSelector(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }


    @Override
    public Set<Schematic> select(Player player, SchematicRegistry registry) {
        return registry.getCache(SchematicBrushCache.key).getSchematicsByName(player, term());
    }

    @Override
    public String asComponent() {
        return MessageComposer.create()
                .text("  <%s>Name: <%s>%s", Colors.NAME, Colors.VALUE, term())
                .build();
    }

    @Override
    public String name() {
        return "Name";
    }

    @Override
    public String descriptor() {
        return term();
    }
}
