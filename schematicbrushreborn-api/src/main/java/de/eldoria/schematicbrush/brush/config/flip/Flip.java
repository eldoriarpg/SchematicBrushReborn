/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.eldoria.schematicbrush.brush.config.flip;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Direction;
import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.util.Shiftable;

/**
 * Represents a flip of a schematic.
 */
public interface Flip extends Shiftable<Flip> {
    /**
     * No flip.
     */
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
        public void shift() {
        }

        @Override
        public String toString() {
            return "NONE";
        }
    };

    /**
     * A Flip from east to west.
     */
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
        public String toString() {
            return "EAST WEST";
        }
    };

    /**
     * A flip from north to south.
     */
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
        public String toString() {
            return "NORTH SOUTH";
        }
    };

    /**
     * A flip from up to down.
     */
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
        public String toString() {
            return "UP DOWN";
        }
    };

    static Flip[] values() {
        return new Flip[]{NONE, NORTH_SOUTH, EAST_WEST, UP_DOWN};
    }

    /**
     * Parse a string to a valid flip value
     *
     * @param input string to parse
     * @return flip enum value
     * @throws IllegalArgumentException when the value can't be parsed.
     * @throws CommandException         when the flip type is invalid
     */
    static Flip asFlip(String input) throws CommandException {
        for (var value : values()) {
            if (value.name().equals(input)) return value;
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

    /**
     * Simple name of the flip. Can not contain spaces
     *
     * @return name
     */
    String name();

    /**
     * Alias which is used for parsing only
     *
     * @return array of aliases
     */
    String[] alias();

    /**
     * The direction as vector
     *
     * @return direction
     */
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
}
