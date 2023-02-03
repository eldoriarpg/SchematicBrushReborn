/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SerializableAs("sbrOffsetRange")
public class OffsetRange extends AOffset {

    private final int min;
    private final int max;

    public OffsetRange(int min, int max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    public OffsetRange(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        min = map.getValueOrDefault("min", -1);
        max = map.getValueOrDefault("max", 1);
        value(valueProvider());
    }

    @Override
    public boolean shiftable() {
        return true;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("min", min)
                .add("max", max)
                .build();
    }

    @Override
    public void shift() {
        if (value() + 1 > max) {
            value(min);
        } else {
            value(value() + 1);
        }
    }


    @Override
    public Integer valueProvider() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    @Override
    public String descriptor() {
        return String.format("%s » %s", min, max);
    }

    @Override
    public String name() {
        return "Range";
    }

    @Override
    public Mutator<Integer> copy() {
        return new OffsetRange(min, max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetRange range)) return false;
        if (!super.equals(o)) return false;

        if (min != range.min) return false;
        return max == range.max;
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + min;
        result = 31 * result + max;
        return result;
    }
}
