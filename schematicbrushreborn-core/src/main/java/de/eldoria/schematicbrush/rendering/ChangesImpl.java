/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import com.fastasyncworldedit.core.queue.implementation.packet.ChunkPacket;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.schematicbrush.util.FAWE;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChangesImpl implements Changes {
    private final com.sk89q.worldedit.util.Location location;
    private final Map<Location, BlockData> changed;
    private final Map<Location, BlockData> original;

    private ChangesImpl(com.sk89q.worldedit.util.Location location, Map<Location, BlockData> changed, Map<Location, BlockData> original) {
        this.location = location;
        this.changed = changed;
        this.original = original;
    }

    @Override
    public Location location() {
        return BukkitAdapter.adapt(location);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int show(Player player) {
        return sendChanges(player, changed);
    }

    @Override
    public int hide(Player player) {
        return sendChanges(player, original);
    }

    @Override
    public int size() {
        return changed.size();
    }

    private int sendChanges(Player player, Map<Location, BlockData> data) {
        if (FAWE.isFawe()) {
            // in an ideal world would be some cool FAWE stuff here. Sadly it isn't.
            return sendBlocks(player, data);
        } else {
            return sendBlocks(player, data);
        }
    }

    private void sendChunks(Player player, Map<Location, BlockData> data) {
        // Time wasted: 2 hours.
        Map<Pair<Integer, Integer>, ChunkPacket> packetMap = new HashMap<>();

        for (var entry : data.entrySet()) {
            int x = entry.getKey().getBlockX() << 4;
            int z = entry.getKey().getBlockZ() << 4;
            //packetMap.computeIfAbsent(Pair.of(x,z), k -> new ChunkPacket(x,z,() -> ))
        }
    }

    private int sendBlocks(Player player, Map<Location, BlockData> data) {
        for (var entry : data.entrySet()) {
            player.sendBlockChange(entry.getKey(), entry.getValue());
        }
        return data.size();
    }

    @Override
    public int show(Player player, Changes oldChanges) {
        var filter = new HashMap<>(changed);
        for (var entry : oldChanges.changed().entrySet()) {
            filter.remove(entry.getKey(), entry.getValue());
        }
        return sendChanges(player, filter);
    }

    @Override
    public int hide(Player player, Changes newChanges) {
        var filter = new HashMap<>(original);
        for (var location : newChanges.changed().keySet()) {
            filter.remove(location);
        }
        return sendChanges(player, filter);
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

        public Changes build(com.sk89q.worldedit.util.Location location) {
            return new ChangesImpl(location, changed, original);
        }
    }


}
