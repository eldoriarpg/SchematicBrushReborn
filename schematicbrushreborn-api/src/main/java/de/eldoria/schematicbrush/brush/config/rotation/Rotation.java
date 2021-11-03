package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.eldoutilities.commands.exceptions.CommandException;
import de.eldoria.schematicbrush.brush.config.util.IShiftable;

/**
 * Represents a rotation.
 */
public interface Rotation extends IShiftable<Rotation> {
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
     */
    static Rotation parse(String value) throws CommandException {
        switch (value) {
            case "0":
                return ROT_ZERO;
            case "270":
                return ROT_LEFT;
            case "90":
                return ROT_RIGHT;
            case "180":
                return ROT_HALF;
            default:
                throw CommandException.message(value + " is not a value of Rotation");
        }
    }

    /**
     * Get a string as rotation value.
     *
     * @param value value to parse
     * @return rotation enum
     * @throws IllegalArgumentException when value can't be parsed
     */
    static Rotation valueOf(int value) {
        switch (value) {
            case 0:
                return ROT_ZERO;
            case 270:
                return ROT_LEFT;
            case 90:
                return ROT_RIGHT;
            case 180:
                return ROT_HALF;
        }
        return ROT_ZERO;
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
