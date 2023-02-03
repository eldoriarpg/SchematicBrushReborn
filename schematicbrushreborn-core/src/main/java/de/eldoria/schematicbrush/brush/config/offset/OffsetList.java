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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SerializableAs("sbrOffsetList")
public class OffsetList extends AOffset {

    private final List<Integer> values;

    public OffsetList(List<Integer> values) {
        this.values = values;
    }

    public OffsetList(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        values = map.getValue("values");
        value(valueProvider());
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
    public boolean shiftable() {
        return true;
    }

    @Override
    public void shift() {
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

    @Override
    public Mutator<Integer> copy() {
        return new OffsetList(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OffsetList offsetList)) return false;
        if (!super.equals(o)) return false;

        return values.equals(offsetList.values);
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }
}
