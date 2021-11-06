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

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.weather.WeatherType;
import org.bukkit.World;

public class FakeWorldImpl extends BukkitWorld implements FakeWorld {
    private final ChangesImpl.Builder changes = ChangesImpl.builder();

    /**
     * Construct the object.
     *
     * @param world the world
     */
    public FakeWorldImpl(World world) {
        super(world);
    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, boolean notifyAndLight) {
        var data = BukkitAdapter.adapt(block.toBaseBlock());
        var location = BukkitAdapter.adapt(getWorld(), position);
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

    @Override
    public Changes changes() {
        return changes.build();
    }


}
