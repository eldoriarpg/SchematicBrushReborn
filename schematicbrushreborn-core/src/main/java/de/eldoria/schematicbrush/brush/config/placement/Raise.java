/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

public class Raise extends APlacement {
    public Raise() {
    }
    @Override
    public int find(Clipboard clipboard) {
        var dimensions = clipboard.getDimensions();
        for (var y = dimensions.y() - 1; y > -1; y--) {
            if (levelNonAir(clipboard, dimensions, y)) return y;
        }
        return dimensions.y();
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
