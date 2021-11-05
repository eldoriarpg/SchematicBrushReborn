package de.eldoria.schematicbrush.brush.config.blockfilter;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.function.mask.Mask;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.PasteMutation;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Level;

public class BlockFilter implements Mutator<String> {
    private static final WorldEdit WORLD_EDIT = WorldEdit.getInstance();
    String maskString;
    Mask mask;

    @Override
    public void invoke(PasteMutation mutation) {
        var mask = mask(mutation);
        // TODO: Iterate over clipboard and change blocks if required.
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public String descriptor() {
        return null;
    }

    @Override
    public void value(String value) {

    }

    @Override
    public String value() {
        return null;
    }

    @Override
    public String valueProvider() {
        return null;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    private Mask mask(PasteMutation mutation) {
        try {
            mask = WORLD_EDIT.getMaskFactory().parseFromInput(maskString, mutation.parserContext());
        } catch (InputParseException e) {
            SchematicBrushReborn.logger().log(Level.WARNING, "Could not parse saved mask " + maskString + ".", e);
        }
            return vector -> false;
    }
}
