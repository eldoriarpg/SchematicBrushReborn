package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.Rotation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class OffsetList extends AOffset {

    private final List<Integer> values;

    public OffsetList(List<Integer> values) {
        this.values = values;
    }

    public OffsetList(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        values = map.getValue("values");
    }

    @Override
    public Integer valueProvider() {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("values", values)
                .build();
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
    @Override
    public String asComponent() {
        return MessageComposer.create()
                .text("  <%s>List: <%s>%s", Colors.NAME, Colors.VALUE,
                        values.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")))
                .build();
    }

    @Override
    public String name() {
        return "List";
    }
}
