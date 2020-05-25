package de.eldoria.schematicbrush.util;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;

public enum Flip implements Randomable {
    NONE,
    EAST_WEST(Direction.EAST, "E", "W", "EW", "WE"),
    NORT_SOUTH(Direction.NORTH, "NS", "SN", "N", "S"),
    RANDOM("*");

    private final String[] alias;
    private final Direction direction;


    Flip(Direction direction, String... alias) {
        this.alias = alias;
        this.direction = direction;
    }

    Flip(String... alias) {
        this(null, alias);
    }

    public Flip getFlipDirection() {
        return this == RANDOM ? values()[randomInt(3)] : this;
    }

    public Vector3 asVector() {
        return direction.toVector();
    }


    public static Flip asFlip(String string) {
        for (Flip value : values()) {
            for (String alias : value.alias) {
                if (alias.equalsIgnoreCase(string)) return value;
            }
        }
        throw new IllegalArgumentException(string + " is not a valid Flip value");
    }
}
