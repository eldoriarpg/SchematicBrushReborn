package de.eldoria.schematicbrush.brush.config.flip;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.util.IShiftable;

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
    static Flip asFlip(String input) throws CommandException {
        for (var value : values()) {
            for (var alias : value.alias()) {
                if (alias.equalsIgnoreCase(input)) return value;
            }
        }
        throw CommandException.message("Invalid flip type");
    }

    static Flip valueOf(String input) {
        for (var value : values()) {
            if (value.name().equals(input)) {
                return value;
            }
        }
        return NONE;
    }

    String name();

    String[] alias();

    Vector3 direction();

    @Override
    default void value(Flip value) {
    }

    @Override
    default Flip value() {
        return this;
    }

    @Override
    default Flip valueProvider() {
        return this;
    }

    Flip NONE = new Flip() {
        @Override
        public String name() {
            return "none";
        }

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

    Flip EAST_WEST = new Flip() {
        @Override
        public String name() {
            return "east";
        }

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

    Flip NORTH_SOUTH = new Flip() {
        @Override
        public String name() {
            return "north";
        }

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

    Flip UP_DOWN = new Flip() {
        @Override
        public String name() {
            return "up";
        }

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
