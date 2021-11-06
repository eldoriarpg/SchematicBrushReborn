/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
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

    private void sendChanges(Player player, Map<Location, BlockData> data) {
        for (var entry : data.entrySet()) {
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
            return new ChangesImpl(changed, original);
        }
    }
}
