package de.eldoria.schematicbrush.brush.config.flip;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import de.eldoria.schematicbrush.brush.config.values.IShiftable;

public interface Flip extends IShiftable<Flip> {
    static Flip[] values() {
        return new Flip[]{NONE, NORTH_SOUTH, EAST_WEST, UP_DOWN};
    }

    /**
     * Parse a string to a valid flip value
     *
     * @param input string to parse
     * @return flip enum value
     * @throws IllegalArgumentException when the value can't be parsed.
     */
    public static Flip asFlip(String input) {
        for (var value : values()) {
            for (var alias : value.alias()) {
                if (alias.equalsIgnoreCase(input)) return value;
            }
        }
        throw new IllegalArgumentException(input + " is not a valid Flip value");
    }    Flip NONE = new Flip() {
        @Override
        public String[] alias() {
            return new String[0];
        }

        @Override
        public Vector3 direction() {
            return Vector3.ZERO;
        }

        @Override
        public Flip shift() {
            return NONE;
        }

        @Override
        public String toString() {
            return "NONE";
        }
    };

    String[] alias();

    Vector3 direction();    Flip EAST_WEST = new Flip() {
        @Override
        public String[] alias() {
            return new String[]{"E", "W", "EW", "WE"};
        }

        @Override
        public Vector3 direction() {
            return Direction.EAST.toVector();
        }

        @Override
        public Flip shift() {
            return NORTH_SOUTH;
        }

        @Override
        public String toString() {
            return "EAST WEST";
        }
    };

    @Override
    default void value(Flip value) {
    }

    @Override
    default Flip value() {
        return this;
    }    Flip NORTH_SOUTH = new Flip() {
        @Override
        public String[] alias() {
            return new String[]{"NS", "SN", "N", "S"};
        }

        @Override
        public Vector3 direction() {
            return Direction.NORTH.toVector();
        }

        @Override
        public Flip shift() {
            return UP_DOWN;
        }

        @Override
        public String toString() {
            return "NORTH SOUTH";
        }
    };

    @Override
    default Flip valueProvider() {
        return this;
    }

    Flip UP_DOWN = new Flip() {
        @Override
        public String[] alias() {
            return new String[]{"U", "D", "UD", "DU"};
        }

        @Override
        public Vector3 direction() {
            return Direction.UP.toVector();
        }

        @Override
        public Flip shift() {
            return EAST_WEST;
        }

        @Override
        public String toString() {
            return "UP DOWN";
        }
    };








}
