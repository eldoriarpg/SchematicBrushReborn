package de.eldoria.schematicbrush.brush.config.flip;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FlipFixed extends AFlip {
    public FlipFixed(Flip flip) {
        super(flip);
    }

    public FlipFixed(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return super.serialize();
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
