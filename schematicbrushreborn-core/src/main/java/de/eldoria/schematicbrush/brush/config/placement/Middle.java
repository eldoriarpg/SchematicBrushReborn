/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C EldoriaRPG Team and Contributor
 */
package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("sbrPlacementMiddle")
public class Middle extends APlacement {
    public Middle() {
    }

    public Middle(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public int find(Clipboard clipboard) {
        return clipboard.getDimensions().getY() / 2;
    }

    @Override
    public String name() {
        return "Middle";
    }

    @Override
    public Mutator<APlacement> copy() {
        return new Middle();
    }
}
