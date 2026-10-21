/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class OffsetList extends AOffset {

    private final List<Integer> values;

    @JsonCreator
    public OffsetList(@JsonProperty("values") List<Integer> values) {
        this.values = values;
    }

    /**
     * The values the offset is chosen from.
     *
     * @return the values the offset is chosen from
     */
    public List<Integer> values() {
        return List.copyOf(values);
    }
    @Override
    public Integer valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
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
