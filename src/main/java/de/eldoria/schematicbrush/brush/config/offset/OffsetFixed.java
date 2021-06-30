package de.eldoria.schematicbrush.brush.config.offset;

class OffsetFixed implements IOffset {
    private final int offset;


    public OffsetFixed(int offset) {
        this.offset = offset;
    }

    @Override
    public int offset() {
        return offset;
    }
}
