/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.schematics;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.BrushPaste;
import de.eldoria.schematicbrush.brush.SchematicBrush;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.SchematicSet;
import de.eldoria.schematicbrush.schematics.Schematic;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class RandomSelection implements SchematicSelection {

    public RandomSelection() {
    }

    @Override
    public Optional<Pair<SchematicSet, Schematic>> nextSchematic(SchematicBrush brush, boolean force) {
        BrushPaste next = brush.nextPaste();
        if (next == null) {
            return getDefaultSchem(brush);
        }
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

    /**
     * Picks a random schematic whose file still exists. Missing ones are removed from the set. The schematic is not
     * loaded here; that happens once, when the paste is prepared.
     */
    private Schematic getRandomSchematic(SchematicSet set) {
        List<Schematic> schematics = set.schematics();
        while (!schematics.isEmpty()) {
            var candidate = schematics.get(randomInt(schematics.size()));
            if (candidate.getFile().isFile()) return candidate;
            SchematicBrushReborn.logger().log(Level.INFO, "Schematic \"" + candidate.path() + "\" does not exist anymore.");
            schematics.remove(candidate);
        }
        return null;
    }

}
