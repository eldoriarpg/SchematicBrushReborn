package de.eldoria.schematicbrush.brush.config.offset;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class OffsetList extends AOffset {

    private final List<Integer> values;

    public OffsetList(List<Integer> values) {
        this.values = values;
    }

    @Override
    public Integer valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    public Integer shift() {
        if (value() == null) {
            return values.get(ThreadLocalRandom.current().nextInt(values.size()));
        }
        var index = values.indexOf(value());
        if (index + 1 == values.size()) {
            return values.get(0);
        }
        return values.get(index + 1);
    }
}
