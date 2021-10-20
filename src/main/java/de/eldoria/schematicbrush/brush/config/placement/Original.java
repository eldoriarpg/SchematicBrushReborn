package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.config.PasteMutation;

public class Original extends APlacement {
    @Override
    public int find(Clipboard clipboard) {
        return 0;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        // do nothing
    }
}
