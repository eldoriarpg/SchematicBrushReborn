package de.eldoria.schematicbrush.brush.config.rotation;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RotationList extends ARotation {
    private final List<Rotation> values;

    public RotationList(List<Rotation> values) {
        this.values = values;
    }

    @Override
    public Rotation valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    public Rotation shift() {
        if (value() == null) {
            return values.get(ThreadLocalRandom.current().nextInt(values.size()));
        }
        var index = values.indexOf(value());
        Rotation newValue;
        if (index + 1 == values.size()) {
            newValue = values.get(0);
        } else {
            newValue = values.get(index + 1);
        }
        value(newValue);
        return value();
    }
}
