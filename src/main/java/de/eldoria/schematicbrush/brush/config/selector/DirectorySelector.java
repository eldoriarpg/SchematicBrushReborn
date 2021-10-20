package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DirectorySelector extends BaseSelector {
    private final String directory;

    public DirectorySelector(String directory, @Nullable String term, SchematicCache cache) {
        super(term, cache);
        this.directory = directory;
    }

    @Override
    public Set<Schematic> select(Player player) {
        return cache().getSchematicsByDirectory(player, directory, term());
    }
}
