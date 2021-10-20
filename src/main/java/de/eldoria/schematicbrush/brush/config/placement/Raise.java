package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class Raise extends APlacement {
    @Override
    public int find(Clipboard clipboard) {
        var dimensions = clipboard.getDimensions();
        for (var y = dimensions.getBlockY() - 1; y > -1; y--) {
            if (levelNonAir(clipboard, dimensions, y)) return y;
        }
        return dimensions.getBlockY();
    }
}
