/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

import java.util.concurrent.ThreadLocalRandom;

public class OffsetRange extends AOffset {

    private final int min;
    private final int max;

    @JsonCreator
    public OffsetRange(@JsonProperty("min") int min, @JsonProperty("max") int max) {
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    /**
     * Lowest offset.
     *
     * @return lowest offset
     */
    public int min() {
        return min;
    }

    /**
     * Highest offset.
     *
     * @return highest offset
     */
    public int max() {
        return max;
    }
    @Override
    public boolean shiftable() {
        return true;
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
