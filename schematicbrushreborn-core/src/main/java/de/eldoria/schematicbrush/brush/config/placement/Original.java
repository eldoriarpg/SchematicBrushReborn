package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import de.eldoria.schematicbrush.brush.PasteMutation;

import java.util.Map;

public class Original extends APlacement {
    public Original() {
    }

    public Original(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public int find(Clipboard clipboard) {
        return 0;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        // do nothing
    }

    @Override
    public String name() {
        return "Original";
    }
}
