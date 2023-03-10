/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("sbrPlacementDrop")
public class Drop extends APlacement {
    public Drop() {
    }

    public Drop(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public int find(Clipboard clipboard) {
        var dimensions = clipboard.getDimensions();

        for (var y = 0; y < dimensions.getBlockY(); y++) {
            if (levelNonAir(clipboard, dimensions, y)) return y;
        }
        return 0;
    }

    @Override
    public String name() {
        return "Drop";
    }

    @Override
    public Mutator<APlacement> copy() {
        return new Drop();
    }
}
