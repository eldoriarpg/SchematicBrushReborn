package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.EditSession;
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
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.schematicbrush.brush.config.BrushSettings;
import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CapturingExtent implements Extent, BlockChangeCollecter {
    private static final BlockType[] AIR_TYPES = {BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR};
    final Changes.Builder changes = Changes.builder();
    final EditSession session;
    final FakeWorld fakeWorld;
    private final BrushSettings settings;

    public CapturingExtent(EditSession session, FakeWorld fakeWorld, BrushSettings settings) {
        this.session = session;
        this.fakeWorld = fakeWorld;
        this.settings = settings;
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
        return session.createEntity(location, entity);
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
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
        var data = BukkitAdapter.adapt(block.toBaseBlock());
        var location = BukkitAdapter.adapt(fakeWorld.getWorld(), position);
        if ((boolean) settings.getMutator(PlacementModifier.REPLACE_ALL).value()) {
            changes.add(location, location.getBlock().getBlockData(), data);
        } else {
            if (ArrayUtils.contains(AIR_TYPES, getBlock(position).getBlockType())) {
                changes.add(location, location.getBlock().getBlockData(), data);
            }
        }
        return true;
    }

    @Override
    public boolean setBiome(BlockVector2 position, BiomeType biome) {
        return true;
    }

    @Nullable
    @Override
    public Operation commit() {
        return null;
    }

    @Override
    public Changes changes() {
        return changes.build();
    }
}
