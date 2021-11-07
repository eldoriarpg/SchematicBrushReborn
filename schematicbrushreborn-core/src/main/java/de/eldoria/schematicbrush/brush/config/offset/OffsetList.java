/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class OffsetList extends AOffset {

    private final List<Integer> values;

    public OffsetList(List<Integer> values) {
        this.values = values;
    }

    public OffsetList(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        values = map.getValue("values");
    }

    @Override
    public Integer valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("values", values)
                .build();
    }


    @Override
    public void shift() {
        if (value() == null) {
            value(values.get(ThreadLocalRandom.current().nextInt(values.size())));
        }
        var index = values.indexOf(value());
        if (index + 1 == values.size()) {
            value(values.get(0));
        }
        value(values.get(index + 1));
    }

    @Override
    public String descriptor() {
        return values.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    @Override
    public String name() {
        return "List";
    }
}
