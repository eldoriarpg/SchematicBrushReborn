package de.eldoria.schematicbrush.brush.config.parameter;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;

import java.util.function.ToIntFunction;

public enum Placement {
    /**
     * Use the center of the schematic as origin
     */
    MIDDLE(findMiddle(), "m", "centerToTheMiddle"),
    /**
     * Use the lowest center point of the schematic as origin
     */
    BOTTOM(findBottom(), "b", "layItOnTheBottom"),
    /**
     * Use the highest center point of the schematic as origin
     */
    TOP(findTop(), "t", "raiseItToTheTop"),
    /**
     * Use the lowest non air point of the schematic as origin
     */
    DROP(findDrop(), "d", "dropItLikeItsHot"),
    /**
     * Use the highest non air point of the schematic as origin
     */
    RAISE(findRaise(), "r", "raiseItToTheSky"),
    /**
     * Use the origin height as height.
     */
    ORIGINAL(findOriginal(), "o", "whereItWas");
    //TODO: Method to place the schematic relational to the surface.
    //      This means, that when a schematic is placed at a woll it points away from the wall. Same with ceiling.
    //      This will also result in a rotation and not only a offset. This requires a rewrite of placement.
    //      Maybe another placement type e.g. allignment should be used for this to avoid mixup.
    //      This can be then used in combination with placement
    /*
    /**
     * Use the lowest terrain point in the region where the brush should be pasted as y.
     *
    FLOOR(findFloor(), "f", "sinkItInTheGround"),
    /**
     * Use the heighest terrain point in the region where the brush should be pasted as y.
     *
    CEIL(findFloor(), "c");*/
    private final String[] alias;

    private final ToIntFunction<Clipboard> find;
    Placement(ToIntFunction<Clipboard> yCenter, String... alias) {
        this.alias = alias;
        this.find = yCenter;
    }


    /**
     * Get the string as placement type.
     *
     * @param value value to parse
     * @return placement enum value
     * @throws IllegalArgumentException if value cant be parsed
     */
    public static Placement asPlacement(String value) {
        for (Placement placement : values()) {
            if (value.equalsIgnoreCase(placement.toString())) return placement;
            for (String alias : placement.alias) {
                if (alias.equalsIgnoreCase(value)) return placement;
            }
        }
        throw new IllegalArgumentException(value + " is not a enum value or alias.");
    }

    private static ToIntFunction<Clipboard> findOriginal() {
        return clipboard -> clipboard.getOrigin().getBlockY();
    }

    private static ToIntFunction<Clipboard> findMiddle() {
        return clipboard -> clipboard.getDimensions().getY() / 2;
    }

    private static ToIntFunction<Clipboard> findBottom() {
        return clipboard -> 0;

    }

    private static ToIntFunction<Clipboard> findTop() {
        return clipboard -> clipboard.getDimensions().getY();

    }

    private static ToIntFunction<Clipboard> findDrop() {
        return clipboard -> {
            BlockVector3 dimensions = clipboard.getDimensions();

            for (int y = 0; y < dimensions.getBlockY(); y++) {
                if (levelNonAir(clipboard, dimensions, y)) return y;
            }
            return 0;
        };
    }

    private static ToIntFunction<Clipboard> findRaise() {
        return clipboard -> {
            BlockVector3 dimensions = clipboard.getDimensions();
            for (int y = dimensions.getBlockY() - 1; y > -1; y--) {
                if (levelNonAir(clipboard, dimensions, y)) return y;
            }
            return dimensions.getBlockY();
        };
    }

    private static boolean levelNonAir(Clipboard clipboard, BlockVector3 dimensions, int y) {
        for (int x = 0; x < dimensions.getBlockX(); x++) {
            for (int z = 0; z < dimensions.getBlockZ(); z++) {
                if (clipboard.getBlock(clipboard.getMinimumPoint().add(x, y, z)).getBlockType() != BlockTypes.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find the y coordinate of a clipboard based on placement type.
     *
     * @param clipboard clipboard which should be pasted
     * @return relative y origin position of clipboard
     */
    public int find(Clipboard clipboard) {
        return this.find.applyAsInt(clipboard);
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
