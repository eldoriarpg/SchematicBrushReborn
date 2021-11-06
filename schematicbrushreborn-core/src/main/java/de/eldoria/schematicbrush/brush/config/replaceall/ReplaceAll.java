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

package de.eldoria.schematicbrush.brush.config.replaceall;

import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ReplaceAll implements Mutator<Boolean> {
    private final boolean value;

    public ReplaceAll(boolean value) {
        this.value = value;
    }

    public ReplaceAll(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        value = map.getValue("value");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", value)
                .build();
    }

    @Override
    public void invoke(PasteMutation mutation) {
        var preBrushMask = mutation.session().getMask();
        // Apply replace mask
        if (!value) {
            // Check if the user has a block mask defined and append if present.
            //Mask mask = WorldEditBrushAdapter.getMask(brushOwner);
            if (preBrushMask instanceof BlockTypeMask blockMask) {
                blockMask.add(BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR);
            } else {
                mutation.session().setMask(
                        new BlockTypeMask(mutation.session(), BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR));
            }
        }
    }

    @Override
    public void value(Boolean value) {

    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public Boolean valueProvider() {
        return value;
    }

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public String descriptor() {
        return String.format("%s", value);
    }
}
