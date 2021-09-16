package de.eldoria.schematicbrush.brush.config;

import com.sk89q.worldedit.WorldEditException;
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
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InvisiblePasteExtent implements Extent {
    @Override
    public BlockVector3 getMinimumPoint() {
        return null;
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return null;
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return null;
    }

    @Override
    public List<? extends Entity> getEntities() {
        return null;
    }

    @Nullable
    @Override
    public Entity createEntity(Location location, BaseEntity entity) {
        return null;
    }

    @Override
    public BlockState getBlock(BlockVector3 position) {
        return null;
    }

    @Override
    public BaseBlock getFullBlock(BlockVector3 position) {
        return null;
    }

    @Override
    public BiomeType getBiome(BlockVector2 position) {
        return null;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
        BlockData adapt = BukkitAdapter.adapt(block);

        return true;
    }

    @Override
    public boolean setBiome(BlockVector2 position, BiomeType biome) {
        return false;
    }

    @Nullable
    @Override
    public Operation commit() {
        return null;
    }
}
