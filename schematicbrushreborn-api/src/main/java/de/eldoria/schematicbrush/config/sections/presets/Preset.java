package de.eldoria.schematicbrush.config.sections.presets;

import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Representing a schematic preset which holds multiple schematic sets.
 */
public interface Preset extends ConfigurationSerializable {
    @Override
    @NotNull Map<String, Object> serialize();

    /**
     * Name of the preset. This is unique for each container
     *
     * @return name
     */
    String name();

    /**
     * The preset as a info compnent used for medium details
     *
     * @param global if the preset is a global preset
     * @return component
     */
    String infoComponent(boolean global);

    /**
     * The detailed component with interactable buttons
     *
     * @return component
     */
    String detailComponent();

    /**
     * The simple compnent used for tooltips
     *
     * @return compnent
     */
    String simpleComponent();

    /**
     * Description of the preset
     *
     * @return description
     */
    String description();

    /**
     * Set the description
     *
     * @param description the description
     */
    void description(String description);

    /**
     * Get all schematic set builders contained in the preset
     *
     * @return list of presets
     */
    List<SchematicSetBuilder> schematicSets();
}
