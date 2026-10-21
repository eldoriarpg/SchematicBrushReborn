/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;

public class OffsetFixed extends AOffset {
    @JsonCreator
    public OffsetFixed(@JsonProperty("offset") int offset) {
        super(offset);
    }

    @Override
    public Integer valueProvider() {
        return offset;
    }

    @Override
    public String descriptor() {
        return String.format("%s", offset);
    }

    @Override
    public String name() {
        return "Fixed";
    }

    @Override
    public Mutator<Integer> copy() {
        return new OffsetFixed(offset);
    }
}
