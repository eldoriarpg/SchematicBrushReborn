/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("sbrOffsetFixed")
public class OffsetFixed extends AOffset {
    @JsonCreator
    public OffsetFixed(@JsonProperty("offset") int offset) {
        super(offset);
    }

    public OffsetFixed(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", offset)
                .build();
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
