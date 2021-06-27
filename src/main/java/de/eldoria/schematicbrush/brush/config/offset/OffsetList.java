package de.eldoria.schematicbrush.brush.config.offset;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OffsetList implements IOffset{

    private final List<Integer> values;

    public OffsetList(List<Integer> values) {
        this.values = values;
    }

    @Override
    public int offset() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }
}
