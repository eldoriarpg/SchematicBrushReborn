package de.eldoria.schematicbrush.util;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.Iterator;

public final class Clipboards {
    private Clipboards() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static Iterator<BlockVector3> iterate(com.sk89q.worldedit.extent.clipboard.Clipboard clipboard){
        return clipboard.getRegion().iterator();
    }
}
