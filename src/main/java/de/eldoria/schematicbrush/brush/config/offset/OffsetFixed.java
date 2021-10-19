package de.eldoria.schematicbrush.brush.config.offset;

class OffsetFixed extends AOffset {
    private final int offset;

    public OffsetFixed(int offset) {
        this.offset = offset;
    }

    @Override
    public Integer valueProvider() {
        return offset;
    }
}
