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

package de.eldoria.schematicbrush.brush.config.flip;

import com.sk89q.worldedit.math.Vector3;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class AFlip implements Mutator<Flip> {
    protected Flip flip = null;

    public AFlip() {
    }

    public AFlip(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        flip = Flip.valueOf(map.getValue("value"));
    }

    public AFlip(Flip flip) {
        this.flip = flip;
    }

    public static AFlip fixed(Flip flip) {
        return new FlipFixed(flip);
    }

    public static AFlip list(List<Flip> flips) {
        return new FlipList(flips);
    }

    public static AFlip random() {
        return new FlipRandom();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", flip.name())
                .build();
    }

    @Override
    public void value(Flip value) {
        flip = value;
    }

    @Override
    public Flip value() {
        return flip;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        if (value().direction() != Vector3.ZERO) {
            mutation.transform(mutation.transform().scale(value().direction().abs().multiply(-2.0).add(1.0, 1.0, 1.0)));
        }
    }
}
