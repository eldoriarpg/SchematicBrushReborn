package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class OffsetRange extends AOffset {

    private final int min;
    private final int max;

    public OffsetRange(int min, int max) {
        this.min = min;
        this.max = max;
        shift();
    }

    public OffsetRange(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        min = map.getValue("min");
        max = map.getValue("max");
        shift();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }


    @Override
    public Integer valueProvider() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    @Override
    public String descriptor() {
        return String.format("%s-%s", min, max);
    }

    @Override
    public String name() {
        return "Range";
    }
}
