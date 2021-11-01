package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RotationList extends ARotation {
    private final List<Rotation> values;

    public RotationList(List<Rotation> values) {
        this.values = values;
    }

    public RotationList(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<Integer> values = map.getValue("values");
        this.values = values.stream().map(Rotation::valueOf).collect(Collectors.toList());
    }

    @Override
    public Rotation valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    public void shift() {
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

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("values", values.stream().map(Rotation::degree).collect(Collectors.toList()))
                .build();
    }

    @Override
    public String descriptor() {
        return values.stream().map(Rotation::degree).map(String::valueOf).collect(Collectors.joining(", "));
    }

    @Override
    public String name() {
        return "list";
    }
}
