package de.eldoria.schematicbrush.brush.config.parameter;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import de.eldoria.schematicbrush.util.Randomable;

public enum Flip implements Randomable {
    NONE(),
    EAST_WEST(Direction.EAST, "E", "W", "EW", "WE"),
    NORT_SOUTH(Direction.NORTH, "NS", "SN", "N", "S"),
    RANDOM("*");
    //TODO: Flip Upside down as well but exclude this in random.
    //      Add combinations of upside_down x east_west and upside_down x north_south

    private final String[] alias;
    private final Direction direction;


    Flip(Direction direction, String... alias) {
        this.alias = alias;
        this.direction = direction;
    }

    Flip(String... alias) {
        this(null, alias);
    }

    /**
     * Parse a string to a valid flip value
     *
     * @param string string to parse
     *
     * @return flip enum value
     *
     * @throws IllegalArgumentException when the value can't be parsed.
     */
    public static Flip asFlip(String string) {
        for (Flip value : values()) {
            for (String alias : value.alias) {
                if (alias.equalsIgnoreCase(string)) return value;
            }
        }
        throw new IllegalArgumentException(string + " is not a valid Flip value");
    }

    /**
     * Get the flip direct. Direction will be random if {@link #RANDOM}
     *
     * @return flip direction
     */
    public Flip getFlipDirection() {
        return this == RANDOM ? values()[randomInt(3)] : this;
    }

    /**
     * Get the direction as a direction vector
     *
     * @return dircetion vector
     */
    public Vector3 asVector() {
        return direction.toVector();
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
