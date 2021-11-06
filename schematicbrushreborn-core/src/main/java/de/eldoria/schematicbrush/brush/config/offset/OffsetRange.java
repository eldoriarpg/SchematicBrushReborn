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

package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class OffsetRange extends AOffset {

    private final int min;
    private final int max;

    public OffsetRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public OffsetRange(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        min = map.getValue("min");
        max = map.getValue("max");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }


    @Override
    public Integer valueProvider() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    @Override
    public String descriptor() {
        return String.format("%s-%s", min, max);
    }

    @Override
    public String name() {
        return "Range";
    }
}
