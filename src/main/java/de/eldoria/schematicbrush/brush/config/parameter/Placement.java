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
    RAISE(findRaise(), "r", "raiseItToTheSky");
    /**
     * Use the lowest terrain point in the region where the brush should be pasted as y.
     */
    //FLOOR(findFloor(), "f", "sinkItInTheGround"),
    /**
     * Use the heighest terrain point in the region where the brush should be pasted as y.
     */
    //CEIL(findFloor(), "c");

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

    /**
     * Find the y coordinate of a clipboard based on placement type.
     * @param clipboard clipboard which should be pasted
     * @return relative y origin position of clipboard
     */
    public int find(Clipboard clipboard) {
        return this.find.applyAsInt(clipboard);
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
                for (int x = 0; x < dimensions.getBlockX(); x++) {
                    for (int z = 0; z < dimensions.getBlockZ(); z++) {
                        if (clipboard.getBlock(clipboard.getMinimumPoint().add(x, y, z)).getBlockType() != BlockTypes.AIR) {
                            return y;
                        }
                    }
                }
            }
            return 0;
        };
    }

    private static ToIntFunction<Clipboard> findRaise() {
        return clipboard -> {
            BlockVector3 dimensions = clipboard.getDimensions();

            for (int y = dimensions.getBlockY() - 1; y > -1; y--) {
                for (int x = 0; x < dimensions.getBlockX(); x++) {
                    for (int z = 0; z < dimensions.getBlockZ(); z++) {
                        if (clipboard.getBlock(clipboard.getMinimumPoint().add(x, y, z)).getBlockType() != BlockTypes.AIR) {
                            return y;
                        }
                    }
                }
            }
            return dimensions.getBlockY();
        };
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
