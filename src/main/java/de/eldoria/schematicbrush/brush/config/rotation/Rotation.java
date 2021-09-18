package de.eldoria.schematicbrush.brush.config.rotation;

import de.eldoria.schematicbrush.brush.config.values.IShiftable;

public interface Rotation extends IShiftable<Rotation> {
    /**
     * Get a string as rotation value.
     *
     * @param value value to parse
     * @return rotation enum
     * @throws IllegalArgumentException when value cant be parsed
     */
    static Rotation asRotation(String value) {
        if ("0".equals(value)) return ROT_ZERO;
        if ("270".equals(value)) return ROT_LEFT;
        if ("90".equals(value)) return ROT_RIGHT;
        if ("180".equals(value)) return ROT_HALF;
        throw new IllegalArgumentException(value + " is not a value of Rotation");
    }

    /**
     * Rotation represented as postive int value.
     *
     * @return rotation as positive integer
     */
    int degree();    /**
     * Represents a rotation of 0.
     */
    Rotation ROT_ZERO = new Rotation() {
        @Override
        public int degree() {
            return 0;
        }

        @Override
        public Rotation shift() {
            return ROT_RIGHT;
        }

        @Override
        public String toString() {
            return "Rot 0";
        }
    };

    @Override
    default void value(Rotation value) {
    }

    @Override
    default Rotation value() {
        return this;
    }    /**
     * Represents a rotation of 90 degrees counter clockwise. Alterantive a rotation of -90 or 270 degrees.
     */
    Rotation ROT_LEFT = new Rotation() {
        @Override
        public int degree() {
            return 270;
        }

        @Override
        public Rotation shift() {
            return ROT_ZERO;
        }

        @Override
        public String toString() {
            return "Rot 270";
        }
    };

    @Override
    default Rotation valueProvider() {
        return this;
    }

    /**
     * Represents a rotation of 180 degrees.
     */
    Rotation ROT_HALF = new Rotation() {
        @Override
        public int degree() {
            return 180;
        }

        @Override
        public Rotation shift() {
            return ROT_LEFT;
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




}
