package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SnapshotContainer {
    private final World world;
    private final Map<BlockVector2, ChunkSnapshot> snapshots = new HashMap<>();

    public SnapshotContainer(World world) {
        this.world = world;
    }

    public void addSnapshot(BlockVector2 coord, Function<BlockVector2, ChunkSnapshot> snapshot) {
        snapshots.computeIfAbsent(coord, snapshot);
    }

    public void addSnapshot(Location location) {
        addSnapshot(chunkCoord(location), k -> getSnapshot(location));
    }

    private BlockVector2 chunkCoord(Location location) {
        return BlockVector2.at(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public static SnapshotContainer fromLocations(World world, Collection<Location> locations) {
        var container = new SnapshotContainer(world);
        for (var location : locations) {
            container.addSnapshot(location);
        }
        return container;
    }

    private ChunkSnapshot getSnapshot(Location location) {
        return world.getChunkAt(location).getChunkSnapshot();
    }

    private ChunkSnapshot getSnapshot(BlockVector2 vector) {
        return snapshots.get(vector);
    }

    private BlockData getBlockData(Location loc) {
        var chunkSnapshot = snapshots.get(chunkCoord(loc));
        var inner = toInnerChunkCoord(loc);
        return chunkSnapshot.getBlockData(inner.getBlockX(), inner.getBlockY(), inner.getBlockZ());
    }

    public Map<Location, BlockData> getBlocks(Set<Location> changedLocations) {
        return changedLocations.stream().collect(Collectors.toMap(k -> k, this::getBlockData));
    }

    public static BlockVector3 toInnerChunkCoord(Location location) {
        return toInnerChunkCoord(BukkitAdapter.adapt(location).toVector().toBlockPoint());
    }

    public static BlockVector3 toInnerChunkCoord(BlockVector3 vec) {
        return BlockVector3.at(toInnerChunkCoord(vec.getBlockX()), vec.getBlockY(), toInnerChunkCoord(vec.getBlockZ()));
    }

    private static int toInnerChunkCoord(int value){
        if (value >= 0) {
            return value % 16;
        }
        value = (value % 16);
        return value < 0 ? 16 + value : value;
    }
}
