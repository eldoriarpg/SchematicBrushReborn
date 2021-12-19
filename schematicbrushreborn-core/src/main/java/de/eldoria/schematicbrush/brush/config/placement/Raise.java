/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;

import java.util.Map;

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
}
