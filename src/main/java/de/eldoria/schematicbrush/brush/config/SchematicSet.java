package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.builder.SchematicSetBuilder;
import de.eldoria.schematicbrush.brush.config.selector.Selector;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.util.Randomable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import javax.naming.Name;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The schematic set represents a part of a brush, which will be combined to a brush by the {@link BrushSettings} A
 * brush can have a weight which indicates how often it should be used, when a {@link BrushSettings} contains more than
 * one brush. The brush contains one ore more schematics which will be provided by random.
 */
public class SchematicSet implements Randomable {

    /**
     * A list of schematics.
     */
    private final List<Schematic> schematics;
    private final Selector selector;
    private final Map<? extends Nameable, Mutator<?>> schematicModifier;
    /**
     * Weight of the brush. Must be always larger then 1. Is -1 when no weight is applied.
     */
    private int weight;

    public SchematicSet(Set<Schematic> schematics, Selector selector, Map<? extends Nameable, Mutator<?>> schematicModifier, int weight) {
        this.schematics = new ArrayList<>(schematics);
        this.selector = selector;
        this.schematicModifier = schematicModifier;
        this.weight = weight;
    }

    public Mutator getMutator(SchematicModifier type) {
        return schematicModifier.get(type);
    }

    public Schematic getRandomSchematic() {
        if (schematics.isEmpty()) return null;

        Clipboard clipboard = null;

        Schematic randomSchematic = null;
        // Search for loadable schematic. Should be likely always the first one.
        while (clipboard == null && !schematics.isEmpty()) {
            randomSchematic = schematics.get(randomInt(schematics.size()));
            try {
                clipboard = randomSchematic.loadSchematic();
            } catch (IOException e) {
                // Silently fail and search for another schematic.
                SchematicBrushReborn.logger().info("Schematic \"" + randomSchematic.path() + "\" does not exist anymore.");
                schematics.remove(randomSchematic);
            }
        }

        return randomSchematic;
    }

    /**
     * Update a not weighted brush.
     *
     * @param weight weight to set.
     * @throws IllegalStateException    when the weight is not -1 and this method is called.
     * @throws IllegalArgumentException when the weight is less than 1.
     */
    public void updateWeight(int weight) throws IllegalStateException, IllegalArgumentException {
        if (this.weight != -1) {
            throw new IllegalStateException("Weight can only be changed if it's the default value.");
        }

        if (weight < 1) {
            throw new IllegalArgumentException("Weight can't be less than 1");
        }

        this.weight = weight;
    }

    public List<Schematic> schematics() {
        return schematics;
    }

    public int weight() {
        return weight;
    }

    public void mutate(PasteMutation mutation) {
        schematicModifier.values().forEach(m -> m.invoke(mutation));
    }

    public SchematicSetBuilder toBuilder() {
        var builder = new SchematicSetBuilder(selector);
        schematicModifier.forEach(builder::withMutator);
        return builder;
    }
}
