package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.schematicbrush.brush.config.PasteMutation;
import de.eldoria.schematicbrush.brush.config.SchematicMutator;

import java.util.function.ToIntFunction;

public abstract class APlacement implements SchematicMutator<APlacement> {
    public abstract int find(Clipboard clipboard);

    @Override
    public void invoke(PasteMutation mutation) {
        var clipboard = mutation.clipboard();
        var dimensions = clipboard.getDimensions();

        var centerZ = clipboard.getMinimumPoint().getBlockZ() + dimensions.getBlockZ() / 2;
        var centerX = clipboard.getMinimumPoint().getBlockX() + dimensions.getBlockX() / 2;
        var centerY = clipboard.getMinimumPoint().getBlockY() + find(clipboard);
        clipboard.setOrigin(BlockVector3.at(centerX, centerY, centerZ));
    }

    protected static boolean levelNonAir(Clipboard clipboard, BlockVector3 dimensions, int y) {
        for (var x = 0; x < dimensions.getBlockX(); x++) {
            for (var z = 0; z < dimensions.getBlockZ(); z++) {
                if (clipboard.getBlock(clipboard.getMinimumPoint().add(x, y, z)).getBlockType() != BlockTypes.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public APlacement shift() {
        return this;
    }

    @Override
    public void value(APlacement value) {
    }

    @Override
    public APlacement value() {
        return this;
    }

    @Override
    public APlacement valueProvider() {
        return this;
    }
}
