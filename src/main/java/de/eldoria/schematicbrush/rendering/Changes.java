package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Changes {
    private final Map<Location, BlockData> changed;
    private final Map<Location, BlockData> original;

    private Changes(Map<Location, BlockData> changed, Map<Location, BlockData> original) {
        this.changed = changed;
        this.original = original;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void show(Player player) {
        sendChanges(player, changed);
    }

    public void hide(Player player) {
        sendChanges(player, original);
    }

    private void sendChanges(Player player, Map<Location, BlockData> data) {
        for (Map.Entry<Location, BlockData> entry : data.entrySet()) {
            player.sendBlockChange(entry.getKey(), entry.getValue());
        }
    }

    public static class Builder {
        private final Map<Location, BlockData> changed = new HashMap<>();
        private final Map<Location, BlockData> original = new HashMap<>();

        public void add(Location location, BlockData original, BlockData changed) {
            if (original.matches(changed)) return;
            this.original.put(location, original);
            this.changed.put(location, changed);
        }

        public Changes build() {
            return new Changes(changed, original);
        }
    }
}
