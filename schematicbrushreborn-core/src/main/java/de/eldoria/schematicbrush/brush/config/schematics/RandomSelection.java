/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.schematics;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.schematics.Schematic;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class RandomSelection implements SchematicSelection {

    public RandomSelection() {
    }

    /**
     * Constructor required by {@link ConfigurationSerializable} in order to deserialize the object.
     */
    @SuppressWarnings("unused")
    public RandomSelection(Map<String, Object> objectMap) {
    }

    @Override
    public Optional<Pair<SchematicSet, Schematic>> nextSchematic(SchematicBrush brush, boolean force) {
        var set = getRandomSchematicSet(brush.settings());
        var schematic = getRandomSchematic(set);
        if (schematic == null) {
            return Optional.empty();
        }

        var newSchematic = getRandomSchematic(set);
        while (newSchematic == schematic) {
            newSchematic = getRandomSchematic(set);
            if (set.schematics().isEmpty()) {
                MessageSender.getPluginMessageSender(SchematicBrushReborn.class).sendError(brush.brushOwner(),
                        "No valid schematic remaining in current set");
                return Optional.empty();
            }
            if (newSchematic == null) continue;
            if (set.schematics().size() <= 1) break;
        }

        return Optional.of(Pair.of(set, schematic));
    }

    private SchematicSet getRandomSchematicSet(BrushSettings settings) {
        var random = randomInt(settings.totalWeight());
        List<SchematicSet> schematicSets = settings.schematicSets();

        var count = 0;
        for (var brush : schematicSets) {
            if (count + brush.weight() > random) {
                return brush;
            }
            count += brush.weight();
        }
        return schematicSets.get(schematicSets.size() - 1);
    }

    private Schematic getRandomSchematic(SchematicSet set) {
        List<Schematic> schematics = set.schematics();
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
                SchematicBrushReborn.logger().log(Level.INFO, "Schematic \"" + randomSchematic.path() + "\" does not exist anymore.", e);
                schematics.remove(randomSchematic);
            } catch (Exception e) {
                SchematicBrushReborn.logger().log(Level.SEVERE, "A critical error occured when loading \"" + randomSchematic.path() + "\".", e);
                schematics.remove(randomSchematic);
            }
        }

        return randomSchematic;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }
}
