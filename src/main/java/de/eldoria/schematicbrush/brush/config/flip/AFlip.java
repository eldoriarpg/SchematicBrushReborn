package de.eldoria.schematicbrush.brush.config.flip;

import com.sk89q.worldedit.math.Vector3;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.PasteMutation;

import java.util.List;

public abstract class AFlip implements Mutator<Flip> {
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

    @Override
    public void invoke(PasteMutation mutation) {
        if (value().direction() != Vector3.ZERO) {
            mutation.transform(mutation.transform().scale(value().direction().abs().multiply(-2).add(1, 1, 1)));
        }
    }
}
