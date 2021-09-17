package de.eldoria.schematicbrush.brush.config.parameter;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import de.eldoria.schematicbrush.brush.config.values.IShiftable;
import de.eldoria.schematicbrush.util.Randomable;
import jdk.javadoc.internal.doclets.formats.html.EnumConstantWriterImpl;

import java.util.function.Supplier;

public enum Flip implements Randomable, IShiftable<Flip> {
    NONE(),
    EAST_WEST(Direction.EAST, "E", "W", "EW", "WE"),
    NORT_SOUTH(Direction.NORTH, "NS", "SN", "N", "S"),
    RANDOM("*");
    //TODO: Flip Upside down as well but exclude this in random.
    //      Add combinations of upside_down x east_west and upside_down x north_south

    private final String[] alias;
    private final Direction direction;
    private final Supplier<Flip> next;

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
     * @param input string to parse
     * @return flip enum value
     * @throws IllegalArgumentException when the value can't be parsed.
     */
    public static Flip asFlip(String input) {
        for (Flip value : values()) {
            for (String alias : value.alias) {
                if (alias.equalsIgnoreCase(input)) return value;
            }
        }
        throw new IllegalArgumentException(input + " is not a valid Flip value");
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
        if(direction == null) return Vector3.ZERO;
        return direction.toVector();
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    @Override
    public Flip shift() {
        return IShiftable.super.shift();
    }

    @Override
    public void value(Flip value) {

    }

    @Override
    public Flip value() {
        return null;
    }

    @Override
    public Flip valueProvider() {
        return this;
    }
}
