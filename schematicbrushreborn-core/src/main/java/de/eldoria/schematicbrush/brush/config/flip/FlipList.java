/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.flip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SerializableAs("sbrFlipList")
public class FlipList extends AFlip {
    private final List<Flip> values;

    @JsonCreator
    public FlipList(@JsonProperty("values") List<Flip> values) {
        this.values = values;
        value(valueProvider());
    }

    public FlipList(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<String> flips = map.getValue("values");
        values = flips.stream().map(Flip::valueOf).collect(Collectors.toList());
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("values", values.stream().map(Flip::name).collect(Collectors.toList()))
                .build();
    }


    @Override
    public Flip valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    public boolean shiftable() {
        return true;
    }

    @Override
    public void shift() {
        var index = values.indexOf(value());
        Flip newValue;
        if (index + 1 == values.size()) {
            newValue = values.get(0);
        } else {
            newValue = values.get(index + 1);
        }
        value(newValue);
    }

    @Override
    public String descriptor() {
        return values.stream().map(Flip::name).collect(Collectors.joining(", "));
    }

    @Override
    public String name() {
        return "List";
    }

    @Override
    public Mutator<Flip> copy() {
        return new FlipList(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlipList flipList)) return false;

        return values.equals(flipList.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
}
