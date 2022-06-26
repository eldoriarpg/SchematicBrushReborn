/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
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
                .add("directory", directory)
                .build();
    }

    @Override
    public Set<Schematic> select(Player player, SchematicRegistry registry) {
        return registry.getCache(SchematicCache.STORAGE).getSchematicsByDirectory(player, directory, term());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectorySelector that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(directory, that.directory);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (directory != null ? directory.hashCode() : 0);
        return result;
    }
}
