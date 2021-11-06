/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
