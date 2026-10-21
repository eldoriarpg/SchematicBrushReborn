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
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * World for fake pastes. Reads are delegated to the real world, every write is discarded.
 */
public class FakeWorld extends BukkitWorld {

    /**
     * Construct the object.
     *
     * @param world the world
     */
    public FakeWorld(World world) {
        super(world);
    }

    @Override
    public String getNameUnsafe() {
        return null;
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, boolean notifyAndLight) {
        return true;
    }

    @Override
    public void refreshChunk(int chunkX, int chunkZ) {
    }

    @Override
    public IChunkGet get(int x, int z) {
        return BukkitAdapter.adapt(getWorld()).get(x, z);
    }

    @Override
    public void sendFakeChunk(@Nullable Player player, ChunkPacket packet) {
    }

    @Override
    public void flush() {
    }

    @SuppressWarnings("removal")
    @Override
    public boolean setTile(int x, int y, int z, CompoundTag tile) throws WorldEditException {
        return false;
    }
}
