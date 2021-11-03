package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface Preset extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    String name();

    String infoComponent(boolean global);

    String detailComponent();

    String simpleComponent();

    String description();

    void description(String description);

    List<SchematicSetBuilder> schematicSets();
}
