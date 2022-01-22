/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
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
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    public OffsetRange(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        min = map.getValueOrDefault("min", -1);
        max = map.getValueOrDefault("max", 1);
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
}
