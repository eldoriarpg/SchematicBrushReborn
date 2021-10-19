package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;

import java.util.List;

public abstract class AOffset implements IShiftable<Integer> {
    protected int offset;

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
    public Integer value() {
        return offset;
    }

    @Override
    public void value(Integer value) {
        offset = value;
    }
}
