package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.transform.AffineTransform;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.values.IShiftable;

public interface SchematicMutator<T> extends IShiftable<T> {
    void invoke(PasteMutation mutation);
}
