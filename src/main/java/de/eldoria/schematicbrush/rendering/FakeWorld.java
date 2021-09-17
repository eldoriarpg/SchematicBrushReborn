package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.weather.WeatherType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

public class FakeWorld extends BukkitWorld implements BlockChangeCollecter {
    Changes.Builder changes = Changes.builder();

    /**
     * Construct the object.
     *
     * @param world the world
     */
    public FakeWorld(World world) {
        super(world);
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, boolean notifyAndLight) throws WorldEditException {
        BlockData data = BukkitAdapter.adapt(block.toBaseBlock());
        Location location = BukkitAdapter.adapt(getWorld(), position);
        changes.add(location, location.getBlock().getBlockData(), data);
        return true;
    }


    @Override
    public void setWeather(WeatherType weatherType) {

    }

    @Override
    public void setWeather(WeatherType weatherType, long duration) {

    }

    @Override
    public boolean setBiome(BlockVector2 position, BiomeType biome) {
        return true;
    }

    public Changes changes() {
        return changes.build();
    }


}
