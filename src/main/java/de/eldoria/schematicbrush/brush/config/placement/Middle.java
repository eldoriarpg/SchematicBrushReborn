package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class Middle extends APlacement {
    @Override
    public int find(Clipboard clipboard) {
        return clipboard.getDimensions().getY() / 2;
    }
}
