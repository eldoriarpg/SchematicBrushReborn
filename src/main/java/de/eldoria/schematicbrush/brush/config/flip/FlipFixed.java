package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FlipFixed extends AFlip {
    public FlipFixed(Flip flip) {
        this.flip = flip;
    }

    public FlipFixed(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        flip = map.getValue("value");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("value", flip.name())
                .build();
    }

    @Override
    public Flip shift() {
        return flip;
    }

    @Override
    public Flip valueProvider() {
        return flip;
    }

    @Override
    public String descriptor() {
        return flip.name();
    }

    @Override
    public String name() {
        return "Fixed";
    }
}
