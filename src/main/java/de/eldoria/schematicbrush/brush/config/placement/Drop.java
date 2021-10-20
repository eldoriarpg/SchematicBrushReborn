package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;

import java.util.Map;

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
}
