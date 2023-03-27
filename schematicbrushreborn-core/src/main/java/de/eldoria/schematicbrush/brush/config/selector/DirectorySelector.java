/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SerializableAs("sbrDirectorySelector")
public class DirectorySelector extends BaseSelector {
    private final String directory;

    @JsonCreator
    public DirectorySelector(@JsonProperty("directory") String directory,
                             @JsonProperty("term") @Nullable String term) {
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
        return registry.get(SchematicCache.STORAGE).getSchematicsByDirectory(player, directory, term());
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
        if (!(o instanceof DirectorySelector selector)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(directory, selector.directory);
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + (directory != null ? directory.hashCode() : 0);
        return result;
    }
}
