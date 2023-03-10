/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import com.sk89q.jnbt.CompoundTag;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A extend used to capture changes.
 */
public interface CapturingExtent extends Extent, BlockChangeCollector {
    Location location();

    @Override
    BlockVector3 getMinimumPoint();

    @Override
    BlockVector3 getMaximumPoint();

    @Override
    List<? extends Entity> getEntities(Region region);

    @Override
    List<? extends Entity> getEntities();

    @Nullable
    @Override
    Entity createEntity(Location location, BaseEntity entity);

    @Override
    BlockState getBlock(BlockVector3 position);

    @Override
    BaseBlock getFullBlock(BlockVector3 position);

    @Override
    BiomeType getBiome(BlockVector2 position);

    @Override
    <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block);

    // Method only exists in FAWE
    @SuppressWarnings({"unused", "SameReturnValue"})
    default boolean setTile(int x, int y, int z, CompoundTag tile) {
        return false;
    }

    @Override
    boolean setBiome(BlockVector2 position, BiomeType biome);

    @Nullable
    @Override
    Operation commit();

    @Override
    Changes changes();
}
