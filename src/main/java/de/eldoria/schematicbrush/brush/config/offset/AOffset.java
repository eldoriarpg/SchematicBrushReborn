package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;
import de.eldoria.schematicbrush.brush.config.values.IValue;

import java.util.List;

public abstract class AOffset implements IShiftable<Integer> {
    int offset;

    static AOffset range(int min, int max) {
        return new OffsetRange(min, max);
    }

    static AOffset fixed(int value) {
        return new OffsetFixed(value);
    }

    static AOffset list(List<Integer> values) {
        return new OffsetList(values);
    }

    @Override
    public Integer value(){
        return offset;
    }
}
