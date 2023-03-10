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

@SerializableAs("sbrPlacementRaise")
public class Raise extends APlacement {
    public Raise() {
    }

    public Raise(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public int find(Clipboard clipboard) {
        var dimensions = clipboard.getDimensions();
        for (var y = dimensions.getBlockY() - 1; y > -1; y--) {
            if (levelNonAir(clipboard, dimensions, y)) return y;
        }
        return dimensions.getBlockY();
    }

    @Override
    public String name() {
        return "Raise";
    }

    @Override
    public Mutator<APlacement> copy() {
        return new Raise();
    }
}
