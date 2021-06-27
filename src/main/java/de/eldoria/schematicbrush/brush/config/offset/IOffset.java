package de.eldoria.schematicbrush.brush.config.offset;

import java.util.List;

public interface IOffset {
    int offset();

    static IOffset range(int min, int max) {
        return new OffsetRange(min, max);
    }

    static IOffset fixed(int value) {
        return new OffsetFixed(value);
    }

    static IOffset list(List<Integer> values) {
        return new OffsetList(values);
    }
}
