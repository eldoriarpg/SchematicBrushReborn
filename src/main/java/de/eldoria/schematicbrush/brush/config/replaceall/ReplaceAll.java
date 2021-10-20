package de.eldoria.schematicbrush.brush.config.replaceall;

import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.schematicbrush.brush.config.PasteMutation;
import de.eldoria.schematicbrush.brush.config.SchematicMutator;

public class ReplaceAll implements SchematicMutator<Boolean> {
    private final boolean value;

    public ReplaceAll(boolean value) {
        this.value = value;
    }

    @Override
    public void invoke(PasteMutation mutation) {
        var preBrushMask = mutation.session().getMask();
        // Apply replace mask
        if (!value) {
            // Check if the user has a block mask defined and append if present.
            //Mask mask = WorldEditBrushAdapter.getMask(brushOwner);
            if (preBrushMask instanceof BlockTypeMask) {
                var blockMask = (BlockTypeMask) preBrushMask;
                blockMask.add(BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR);
            } else {
                mutation.session().setMask(
                        new BlockTypeMask(mutation.session(), BlockTypes.AIR, BlockTypes.VOID_AIR, BlockTypes.CAVE_AIR));
            }
        }
    }

    @Override
    public void value(Boolean value) {

    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public Boolean valueProvider() {
        return value;
    }
}
