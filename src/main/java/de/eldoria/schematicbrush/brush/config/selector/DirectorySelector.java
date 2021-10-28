package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.schematics.impl.SchematicBrushCache;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class DirectorySelector extends BaseSelector {
    private final String directory;

    public DirectorySelector(String directory, @Nullable String term) {
        super(term);
        this.directory = directory;
    }

    public DirectorySelector(Map<String, Object> objectMap) {
        super(objectMap);
        var map = SerializationUtil.mapOf(objectMap);
        directory = map.getValue("directory");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder(super.serialize())
                .add("directory")
                .build();
    }

    @Override
    public Set<Schematic> select(Player player, SchematicRegistry registry) {
        return registry.getCache(SchematicBrushCache.key).getSchematicsByDirectory(player, directory, term());
    }

    @Override
    public String asComponent() {
        return MessageComposer.create()
                .text("  <%s>Directory: <%s>%s <%s>", Colors.NAME, Colors.VALUE, directory, term())
                .build();
    }

    @Override
    public String name() {
        return "Directory";
    }

    @Override
    public String descriptor() {
        if (term() != null && !term().isBlank()) {
            return directory + " - " + term();
        }
        return directory;
    }
}
