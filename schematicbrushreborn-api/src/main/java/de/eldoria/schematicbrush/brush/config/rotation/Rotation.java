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

package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.util.Shiftable;

/**
 * Represents a rotation.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface Rotation extends Shiftable<Rotation> {
    /**
     * Represents a rotation of 0.
     */
    Rotation ROT_ZERO = new Rotation() {
        @Override
        public int degree() {
            return 0;
        }

        @Override
        public String toString() {
            return "Rot 0";
        }
    };

    /**
     * Represents a rotation of 90 degrees counterclockwise. Alterantive a rotation of -90 or 270 degrees.
     */
    Rotation ROT_LEFT = new Rotation() {
        @Override
        public int degree() {
            return 270;
        }

        @Override
        public String toString() {
            return "Rot 270";
        }
    };

    /**
     * Represents a rotation of 180 degrees.
     */
    Rotation ROT_HALF = new Rotation() {
        @Override
        public int degree() {
            return 180;
        }

        @Override
        public String toString() {
            return "Rot 180";
        }
    };

    /**
     * Represents a rotation of 90 degrees clockwise. Alterantive a rotation of 90 degrees.
     */
    Rotation ROT_RIGHT = new Rotation() {
        @Override
        public int degree() {
            return 90;
        }

        @Override
        public Rotation valueProvider() {
            return ROT_HALF;
        }

        @Override
        public String toString() {
            return "Rot 90";
        }
    };

    /**
     * Get a string as rotation value.
     *
     * @param value value to parse
     * @return rotation enum
     * @throws IllegalArgumentException when value can't be parsed
     * @throws CommandException         when the value can't be parsed
     */
    static Rotation parse(String value) throws CommandException {
        return switch (value) {
            case "0" -> ROT_ZERO;
            case "270" -> ROT_LEFT;
            case "90" -> ROT_RIGHT;
            case "180" -> ROT_HALF;
            default -> throw CommandException.message(value + " is not a value of Rotation");
        };
    }

    /**
     * Get a string as rotation value.
     *
     * @param value value to parse
     * @return rotation enum
     * @throws IllegalArgumentException when value can't be parsed
     */
    static Rotation valueOf(int value) {
        return switch (value) {
            case 270 -> ROT_LEFT;
            case 90 -> ROT_RIGHT;
            case 180 -> ROT_HALF;
            default -> ROT_ZERO;
        };
    }

    /**
     * Rotation represented as positive int value.
     *
     * @return rotation as positive integer
     */
    int degree();

    @Override
    default void value(Rotation value) {
    }

    @Override
    default Rotation value() {
        return this;
    }

    @Override
    default Rotation valueProvider() {
        return this;
    }
}
