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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RotationList extends ARotation {
    private final List<Rotation> values;

    public RotationList(List<Rotation> values) {
        this.values = values;
    }

    public RotationList(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Integer> values = map.getValue("values");
        this.values = values.stream().map(Rotation::valueOf).collect(Collectors.toList());
    }

    @Override
    public Rotation valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    public void shift() {
        if (value() == null) {
            value(values.get(ThreadLocalRandom.current().nextInt(values.size())));
        }
        var index = values.indexOf(value());
        Rotation newValue;
        if (index + 1 == values.size()) {
            newValue = values.get(0);
        } else {
            newValue = values.get(index + 1);
        }
        value(newValue);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("values", values.stream().map(Rotation::degree).collect(Collectors.toList()))
                .build();
    }

    @Override
    public String descriptor() {
        return values.stream().map(Rotation::degree).map(String::valueOf).collect(Collectors.joining(", "));
    }

    @Override
    public String name() {
        return "List";
    }
}
