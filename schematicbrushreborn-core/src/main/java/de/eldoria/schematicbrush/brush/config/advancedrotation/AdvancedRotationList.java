/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.advancedrotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@SerializableAs("sbrRotationList")
public class AdvancedRotationList extends AAdvancedRotation {
    private final List<Rotation> values;

    public AdvancedRotationList(List<RotationSupplier> values) {
        this.values = values;
    }

    public AdvancedRotationList(Map<String, Object> objectMap) {
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
    public boolean shiftable() {
        return true;
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

    @Override
    public Mutator<Rotation> copy() {
        return new AdvancedRotationList(values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdvancedRotationList rotationList)) return false;

        return values.equals(rotationList.values);
    }

    @Override
    public int hashCode() {
        var result = super.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }
}
