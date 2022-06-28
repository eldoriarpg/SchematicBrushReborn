/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChangesImpl implements Changes {
    private final Map<Location, BlockData> changed;
    private final Map<Location, BlockData> original;

    private ChangesImpl(Map<Location, BlockData> changed, Map<Location, BlockData> original) {
        this.changed = changed;
        this.original = original;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void show(Player player) {
        sendChanges(player, changed);
    }

    @Override
    public void hide(Player player) {
        sendChanges(player, original);
    }

    @Override
    public int size() {
        return changed.size();
    }

    private void sendChanges(Player player, Map<Location, BlockData> data) {
        for (var entry : data.entrySet()) {
            player.sendBlockChange(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void show(Player player, Changes oldChanges) {
        var filter = new HashMap<>(changed);
        for (var entry : oldChanges.changed().entrySet()) {
            filter.remove(entry.getKey(), entry.getValue());
        }
        sendChanges(player, filter);
    }

    @Override
    public void hide(Player player, Changes newChanges) {
        var filter = new HashMap<>(original);
        for (var location : newChanges.changed().keySet()) {
            filter.remove(location);
        }
        sendChanges(player, filter);
    }

    @Override
    public Map<Location, BlockData> changed() {
        return changed;
    }

    @Override
    public Map<Location, BlockData> original() {
        return original;
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
            return new ChangesImpl(changed, original);
        }
    }


}
