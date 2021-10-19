package de.eldoria.schematicbrush.brush.config.offset;

import java.util.concurrent.ThreadLocalRandom;

class OffsetRange extends AOffset {

    private final int min;
    private final int max;
    private int offset;

    public OffsetRange(int min, int max) {
        this.min = min;
        this.max = max;
        shift();
    }

    @Override
    public Integer valueProvider() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
