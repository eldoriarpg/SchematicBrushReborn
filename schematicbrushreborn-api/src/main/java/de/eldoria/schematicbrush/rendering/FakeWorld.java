/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.rendering;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.weather.WeatherType;

/**
 * A Fake world which wrapps and delegates to a world.
 */
public interface FakeWorld extends World, BlockChangeCollector {
    @Override
    <B extends BlockStateHolder<B>> boolean setBlock(BlockVector3 position, B block, boolean notifyAndLight);

    @Override
    void setWeather(WeatherType weatherType);

    @Override
    void setWeather(WeatherType weatherType, long duration);

    @Override
    boolean setBiome(BlockVector2 position, BiomeType biome);

    /**
     * Changes applied to the world.
     *
     * @return changes
     */
    Changes changes();
}
