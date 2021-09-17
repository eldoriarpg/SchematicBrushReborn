package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;

import java.util.List;

public abstract class AFlip implements IShiftable<Flip> {
    protected Flip flip;

    public static AFlip fixed(Flip flip) {
        return new FlipFixed(flip);
    }

    public static AFlip list(List<Flip> flips) {
        return new FlipList(flips);
    }

    public static AFlip random() {
        return new FlipRandom();
    }

    @Override
    public void value(Flip value) {
        flip = value;
    }

    @Override
    public Flip value() {
        return flip;
    }
}
