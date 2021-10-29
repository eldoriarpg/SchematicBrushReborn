package de.eldoria.schematicbrush.brush.config.offset;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class OffsetFixed extends AOffset {
    public OffsetFixed(int offset) {
        this.offset = offset;
    }

    public OffsetFixed(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        offset = map.getValue("value");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", offset)
                .build();
    }


    @Override
    public Integer valueProvider() {
        return offset;
    }

    @Override
    public String descriptor() {
        return String.format("%s", offset);
    }

    @Override
    public String name() {
        return "Fixed";
    }
}
