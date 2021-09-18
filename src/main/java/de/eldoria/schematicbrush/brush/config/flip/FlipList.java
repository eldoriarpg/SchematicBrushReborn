package de.eldoria.schematicbrush.brush.config.flip;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FlipList extends AFlip {
    private final List<Flip> values;

    public FlipList(List<Flip> values) {
        this.values = values;
    }

    @Override
    public Flip valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    public Flip shift() {
        if (value() == null) {
            return values.get(ThreadLocalRandom.current().nextInt(values.size()));
        }
        var index = values.indexOf(value());
        Flip newValue;
        if (index + 1 == values.size()) {
            newValue = values.get(0);
        } else {
            newValue = values.get(index + 1);
        }
        value(newValue);
        return value();
    }
}
