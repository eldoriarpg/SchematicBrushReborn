package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FlipList extends AFlip {
    private final List<Flip> values;

    public FlipList(List<Flip> values) {
        this.values = values;
    }

    public FlipList(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        List<String> flips = map.getValue("values");
        values = flips.stream().map(Flip::value).collect(Collectors.toList());
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("values", values.stream().map(Flip::name).collect(Collectors.toList()))
                .build();
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

    @Override
    public String asComponent() {
        return MessageComposer.create()
                .text("  <%s>List: <%s>%s", Colors.NAME, Colors.VALUE,
                        values.stream()
                                .map(Flip::name)
                                .collect(Collectors.joining(", ")))
                .build();
    }

    @Override
    public String name() {
        return "List";
    }
}
