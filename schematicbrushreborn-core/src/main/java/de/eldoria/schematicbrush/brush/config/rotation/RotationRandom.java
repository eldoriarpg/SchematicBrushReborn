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

package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class RotationRandom extends RotationList {
    public RotationRandom() {
        super(Arrays.asList(Rotation.ROT_ZERO, Rotation.ROT_RIGHT, Rotation.ROT_HALF, Rotation.ROT_LEFT));
    }

    public RotationRandom(Map<String, Object> objectMap) {
        this();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder().build();
    }

    @Override
    public String descriptor() {
        return "";
    }

    @Override
    public String name() {
        return "Random";
    }
}
