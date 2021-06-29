package de.eldoria.schematicbrush.brush.config.offset;

import java.util.concurrent.ThreadLocalRandom;

class OffsetRange implements IOffset {

    private final int min;
    private final int max;

    public OffsetRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int offset() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
