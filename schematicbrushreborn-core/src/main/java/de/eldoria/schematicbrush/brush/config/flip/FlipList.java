/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.flip;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FlipList extends AFlip {
    private final List<Flip> values;

    @JsonCreator
    public FlipList(@JsonProperty("values") List<Flip> values) {
        this.values = values;
        value(valueProvider());
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
