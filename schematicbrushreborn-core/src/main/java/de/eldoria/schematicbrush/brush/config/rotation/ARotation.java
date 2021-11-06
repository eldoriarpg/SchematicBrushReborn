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
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class ARotation implements Mutator<Rotation> {
    protected Rotation rotation = null;

    public ARotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public ARotation(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        rotation = Rotation.valueOf(map.getValue("value"));
    }

    public ARotation() {
    }

    public static ARotation fixed(Rotation rotation) {
        return new RotationFixed(rotation);
    }

    public static ARotation list(List<Rotation> rotations) {
        return new RotationList(rotations);
    }

    public static ARotation random() {
        return new RotationRandom();
    }

    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", rotation.degree())
                .build();
    }

    @Override
    public void value(Rotation value) {
        rotation = value;
    }

    @Override
    public Rotation value() {
        return rotation;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        if (rotation.value().degree() != 0) {
            mutation.transform(mutation.transform().rotateY(rotation.value().degree()));
        }
    }
}
