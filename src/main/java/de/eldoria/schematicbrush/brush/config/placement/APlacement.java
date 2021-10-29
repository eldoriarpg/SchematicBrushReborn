package de.eldoria.schematicbrush.brush.config.placement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.eldoutilities.serialization.TypeResolvingMap;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.PasteMutation;
import de.eldoria.schematicbrush.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class APlacement implements Mutator<APlacement> {
    public APlacement() {
    }

    public APlacement(Map<String, Object> objectMap) {
        TypeResolvingMap map = SerializationUtil.mapOf(objectMap);
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

    public abstract int find(Clipboard clipboard);

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }

    @Override
    public void invoke(PasteMutation mutation) {
        var clipboard = mutation.clipboard();
        var dimensions = clipboard.getDimensions();

        var centerZ = clipboard.getMinimumPoint().getBlockZ() + dimensions.getBlockZ() / 2;
        var centerX = clipboard.getMinimumPoint().getBlockX() + dimensions.getBlockX() / 2;
        var centerY = clipboard.getMinimumPoint().getBlockY() + find(clipboard);
        clipboard.setOrigin(BlockVector3.at(centerX, centerY, centerZ));
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

    @Override
    public String descriptor() {
        return name();
    }
}
