/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import com.fastasyncworldedit.core.nbt.FaweCompoundTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Receives a fake paste and collects the blocks it would change into a {@link PreviewSnapshot}.
 * <p>
 * The real world is read through Bukkit and only in loaded chunks, so capturing never loads a chunk. Blocks in unloaded
 * chunks, outside the build height, or equal to the world are left out.
 */
public class CapturingExtent implements Extent {
    private final EditSession session;
    private final World world;
    private final boolean replaceAll;
    private final PreviewSnapshot.Builder snapshot;

    /**
     * Creates an extent for one fake paste.
     *
     * @param session  edit session of the paste
     * @param world    world the paste would change
     * @param settings settings of the brush
     * @param origin   location the paste is rendered at
     */
    public CapturingExtent(EditSession session, World world, BrushSettings settings, Location origin) {
        this.session = session;
        this.world = world;
        this.replaceAll = (boolean) settings.getMutator(PlacementModifier.REPLACE_ALL).value();
        this.snapshot = PreviewSnapshot.builder(BukkitAdapter.adapt(origin));
    }

    /**
     * The blocks collected so far.
     *
     * @return snapshot of the captured blocks
     */
    public PreviewSnapshot snapshot() {
        return snapshot.build();
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return session.getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return session.getMaximumPoint();
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return session.getEntities();
    }

    @Override
    public List<? extends Entity> getEntities() {
        return session.getEntities();
    }

    @Nullable
    @Override
    public Entity createEntity(Location location, BaseEntity entity) {
        return null;
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return session.getBlock(position);
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        return session.getFullBlock(position);
    }

    @Override
    public BiomeType getBiome(BlockVector2 position) {
        return session.getBiome(position);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) {
        int x = position.x();
        int y = position.y();
        int z = position.z();
        if (y < world.getMinHeight() || y >= world.getMaxHeight() || !world.isChunkLoaded(x >> 4, z >> 4)) {
            return true;
        }
        var current = world.getBlockData(x, y, z);
        if (!replaceAll && !current.getMaterial().isAir()) {
            return true;
        }
        var data = BukkitAdapter.adapt(block.toBaseBlock());
        if (!current.matches(data)) {
            snapshot.add(x, y, z, data);
        }
        return true;
    }

    @Override
    public boolean setBiome(BlockVector2 position, BiomeType biome) {
        return true;
    }

    @SuppressWarnings("removal")
    @Override
    public boolean setTile(int x, int y, int z, CompoundTag tile) {
        return false;
    }

    @Override
    public boolean tile(int x, int y, int z, FaweCompoundTag tile) {
        return false;
    }

    @Nullable
    @Override
    public Operation commit() {
        return null;
    }
}
