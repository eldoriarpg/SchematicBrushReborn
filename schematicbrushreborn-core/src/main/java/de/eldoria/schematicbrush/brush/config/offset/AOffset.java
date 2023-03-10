/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class AOffset implements Mutator<Integer> {
    protected int offset;

    public AOffset() {
    }

    public AOffset(int offset) {
        this.offset = offset;
    }

    public AOffset(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        offset = map.getValue("value");
    }

    public static AOffset range(int min, int max) {
        return new OffsetRange(min, max);
    }

    public static AOffset fixed(int value) {
        return new OffsetFixed(value);
    }

    public static AOffset list(List<Integer> values) {
        return new OffsetList(values);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", offset)
                .build();
    }

    @Override
    public @NotNull Integer value() {
        return offset;
    }

    @Override
    public void value(@NotNull Integer value) {
        offset = value;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        mutation.pasteOffset(mutation.pasteOffset().add(0, value(), 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AOffset aOffset)) return false;

        return offset == aOffset.offset;
    }

    @Override
    public int hashCode() {
        return offset;
    }
}
