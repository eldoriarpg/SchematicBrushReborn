/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class OffsetFixed extends AOffset {
    public OffsetFixed(int offset) {
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
}
