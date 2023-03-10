/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import com.fastasyncworldedit.core.queue.IChunkGet;
import com.fastasyncworldedit.core.queue.implementation.packet.ChunkPacket;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class FakeWorldImpl extends BukkitWorld implements FakeWorld {
    private final ChangesImpl.Builder changes = ChangesImpl.builder();
    private Location location;

    /**
     * Construct the object.
     *
     * @param world the world
     */
    public FakeWorldImpl(World world) {
        super(world);
    }

    @Override
    public String getNameUnsafe() {
        return null;
    }

    @Override
    public void location(Location location) {
        this.location = location;
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, boolean notifyAndLight) {
        var data = BukkitAdapter.adapt(block.toBaseBlock());
        var location = BukkitAdapter.adapt(getWorld(), position);
        changes.add(location, location.getBlock().getBlockData(), data);
        return true;
    }

    @Override
    public void refreshChunk(int chunkX, int chunkZ) {

    }

    @Override
    public IChunkGet get(int x, int z) {
        return BukkitAdapter.adapt(getWorld()).get(x,z);
    }

    @Override
    public void sendFakeChunk(@Nullable Player player, ChunkPacket packet) {

    }

    @Override
    public void flush() {

    }

    @Override
    public Changes changes() {
        return changes.build(location);
    }


    @Override
    public boolean setTile(int x, int y, int z, CompoundTag tile) throws WorldEditException {
        return false;
    }
}
