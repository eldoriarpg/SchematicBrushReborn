package de.eldoria.schematicbrush.brush.config.flip;

public class FlipFixed extends AFlip {
    public FlipFixed(Flip flip) {
        this.flip = flip;
    }

    @Override
    public Flip shift() {
        return flip;
    }

    @Override
    public Flip valueProvider() {
        return flip;
    }
}
