package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.SchematicSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Replaced by {@link BrushBuilder}
 */
@Deprecated(forRemoval = true)
public final class BrushSettingsBuilder {
    /**
     * List of all sub brushes this brush has.
     */
    private final List<SchematicSet> brushes;
    private final Map<PlacementModifier, Mutator<?>> placementModifier = new HashMap<>();

    public BrushSettingsBuilder(SchematicSet config) {
        brushes = Collections.singletonList(config);
    }

    public BrushSettingsBuilder() {
        brushes = new ArrayList<>();
    }


    /**
     * Add a brush to the brush configuration.
     *
     * @param brush brush which should be added
     * @return builder instance with brush added
     */
    public BrushSettingsBuilder addBrush(SchematicSet brush) {
        brushes.add(brush);
        return this;
    }

    /**
     * Build the brush configuration.
     *
     * @return A immutable brush config.
     */
    public BrushSettings build() {
        return new BrushSettings(brushes, placementModifier);
    }

    public void setModifier(PlacementModifier type, Mutator<?> mutator) {
        placementModifier.put(type, mutator);
    }
}
