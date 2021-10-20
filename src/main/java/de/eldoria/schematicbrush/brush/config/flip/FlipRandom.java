package de.eldoria.schematicbrush.brush.config.flip;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.schematicbrush.util.Colors;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class FlipRandom extends FlipList {
    public FlipRandom() {
        super(Arrays.asList(Flip.NONE, Flip.EAST_WEST, Flip.NORTH_SOUTH));
    }


    public FlipRandom(Map<String, Object> objectMap) {
        this();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }

    @Override
    public String asComponent() {
        return MessageComposer.create()
                .text("  <%s>Random", Colors.NAME)
                .build();
    }

    @Override
    public String name() {
        return "Random";
    }
}
