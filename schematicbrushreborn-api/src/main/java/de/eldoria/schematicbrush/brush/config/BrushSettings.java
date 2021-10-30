package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.util.Randomable;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A brush configuration represents the settings of a single brush. A brush consists of one or more brushes represented
 * by a {@link SchematicSet} object. If more than one {@link SchematicSet} is present, a random {@link SchematicSet}
 * will be returned via the {@link #getRandomBrushConfig()} based on the {@link SchematicSet#weight()} of the
 * brushes. The brush settings contains some general brush settings, which apply to the whole brush and not only to
 * specific sub brushes.
 */
public final class BrushSettings implements Randomable {
    /**
     * List of all sub brushes this brush has.
     */
    private final List<SchematicSet> schematicSets;

    private final Map<PlacementModifier, Mutator<?>> placementModifier;
    /**
     * The total weight of all brushes in the {@link #schematicSets} list
     */
    private final int totalWeight;

    public BrushSettings(List<SchematicSet> schematicSets, Map<PlacementModifier, Mutator<?>> placementModifier) {
        this.schematicSets = schematicSets;
        this.placementModifier = placementModifier;

        // Count all weights, which have a weight set.
        var totalWeight = schematicSets.stream().filter(b -> b.weight() > 0).mapToInt(SchematicSet::weight).sum();
        // Count all weighted brushes
        var weighted = (int) schematicSets.stream().filter(b -> b.weight() > 0).count();
        // Count all unweighted weight
        var unweighted = (int) schematicSets.stream().filter(b -> b.weight() < 0).count();
        int defaultWeight;
        // Handle case, when no brush is weighted
        if (weighted == 0) {
            defaultWeight = 1;
        } else {
            // Calculate the default weight which is the average of all weightes brushes
            defaultWeight = totalWeight / weighted;
        }

        // Set the weight of all unweighted brushes
        schematicSets.stream().filter(b -> b.weight() < 0).forEach(b -> b.updateWeight(defaultWeight));

        // Calculate the total weight of all brushes
        this.totalWeight = schematicSets.stream().mapToInt(SchematicSet::weight).sum();
    }

    /**
     * Get a random brush from the {@link #schematicSets} list based on their {@link SchematicSet#weight()}.
     *
     * @return a random brush
     */
    public SchematicSet getRandomBrushConfig() {
        var random = randomInt(totalWeight);

        var count = 0;
        for (var brush : schematicSets) {
            if (count + brush.weight() > random) {
                return brush;
            }
            count += brush.weight();
        }
        return schematicSets.get(schematicSets.size() - 1);
    }

    /**
     * Counts all schematics in all brushes. No deduplication.
     *
     * @return total number of schematics in all brushes.
     */
    public int getSchematicCount() {
        return schematicSets.stream().map(b -> b.schematics().size()).mapToInt(Integer::intValue).sum();
    }

    /**
     * Get the brush configuration with a new brush combined. The options from the current brush are used.
     *
     * @param brush Brush to combine. Only the {@link SchematicSet} list is updated.
     * @return new brush configuration.
     */
    public BrushSettings combine(BrushSettings brush) {
        List<SchematicSet> brushes = new ArrayList<>(schematicSets);
        brushes.addAll(brush.schematicSets);
        return new BrushSettings(brushes, placementModifier);
    }

    /**
     * Get the schematic sets of this brush
     *
     * @return list of {@link SchematicSet}
     */
    public List<SchematicSet> schematicSets() {
        return schematicSets;
    }

    /**
     * Get the total weight of all {@link BrushSettings#schematicSets()}
     *
     * @return total weight
     */
    public int totalWeight() {
        return totalWeight;
    }

    /**
     * Get a mutator of the brush
     *
     * @param type type to request
     * @return the mutator.
     */
    public Mutator<?> getMutator(PlacementModifier type) {
        return placementModifier.get(type);
    }

    /**
     * Mutate the {@link PasteMutation} and apply the changes by the {@link BrushSettings#placementModifier}
     *
     * @param mutation mutation instance
     */
    public void mutate(PasteMutation mutation) {
        placementModifier.values().forEach(m -> m.invoke(mutation));
    }

    /**
     * Converts the brush setting to a {@link BrushBuilder} representing all settings applied.
     * @param player player
     * @param settingsRegistry setting registry
     * @param schematicRegistry schematic registry
     * @return new brush builder instance
     */
    public BrushBuilder toBuilder(Player player, BrushSettingsRegistry settingsRegistry, SchematicRegistry schematicRegistry) {
        var brushBuilder = new BrushBuilder(player, settingsRegistry, schematicRegistry);
        placementModifier.forEach(brushBuilder::setPlacementModifier);
        schematicSets.stream().map(SchematicSet::toBuilder).forEach(brushBuilder::addSchematicSet);
        return brushBuilder;
    }
}
