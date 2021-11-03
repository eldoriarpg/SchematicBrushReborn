package de.eldoria.schematicbrush.config.sections;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SchematicConfig extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    void addSource(SchematicSource source);

    List<SchematicSource> getSources();

    String getPathSeparator();

    boolean isPathSourceAsPrefix();

    Optional<SchematicSource> getSourceForPath(Path path);
}
