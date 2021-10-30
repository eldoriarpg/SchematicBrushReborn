package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Changes {
    private final Map<Location, BlockData> changed;

    private Changes(Map<Location, BlockData> changed) {
        this.changed = changed;
    }

    public Set<Location> changedLocations() {
        return changed.keySet();
    }

    public Map<Location, BlockData> changed() {
        return changed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Location, BlockData> changed = new HashMap<>();

        public void add(Location location, BlockData original, BlockData changed) {
            if (original.matches(changed)) return;
            this.changed.put(location, changed);
        }

        public Changes build() {
            return new Changes(changed);
        }
    }
}
