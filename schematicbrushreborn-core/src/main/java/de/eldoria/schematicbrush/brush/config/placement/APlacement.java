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

package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class APlacement implements Mutator<APlacement> {
    public APlacement() {
    }

    public APlacement(Map<String, Object> objectMap) {
    }

    protected static boolean levelNonAir(Clipboard clipboard, BlockVector3 dimensions, int y) {
        for (var x = 0; x < dimensions.getBlockX(); x++) {
            for (var z = 0; z < dimensions.getBlockZ(); z++) {
                if (clipboard.getBlock(clipboard.getMinimumPoint().add(x, y, z)).getBlockType() != BlockTypes.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract int find(Clipboard clipboard);

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }

    @Override
    public void invoke(PasteMutation mutation) {
        var clipboard = mutation.clipboard();
        var dimensions = clipboard.getDimensions();

        var centerZ = clipboard.getMinimumPoint().getBlockZ() + dimensions.getBlockZ() / 2;
        var centerX = clipboard.getMinimumPoint().getBlockX() + dimensions.getBlockX() / 2;
        var centerY = clipboard.getMinimumPoint().getBlockY() + find(clipboard);
        clipboard.setOrigin(BlockVector3.at(centerX, centerY, centerZ));
    }

    @Override
    public void shift() {
    }

    @Override
    public void value(APlacement value) {
    }

    @Override
    public APlacement value() {
        return this;
    }

    @Override
    public APlacement valueProvider() {
        return this;
    }

    @Override
    public String descriptor() {
        return name();
    }
}
