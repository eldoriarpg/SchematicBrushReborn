package de.eldoria.schematicbrush.brush.config.offset;

import com.sk89q.worldedit.math.BlockVector3;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.Mutator;

import java.util.List;

public abstract class AOffset implements Mutator<Integer> {
    protected int offset;

    public static AOffset range(int min, int max) {
        return new OffsetRange(min, max);
    }

    public static AOffset fixed(int value) {
        return new OffsetFixed(value);
    }

    public static AOffset list(List<Integer> values) {
        return new OffsetList(values);
    }

    @Override
    public Integer value() {
        return offset;
    }

    @Override
    public void value(Integer value) {
        offset = value;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        mutation.pasteOffset(BlockVector3.at(0, value(), 0));
    }
}
