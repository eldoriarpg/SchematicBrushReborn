package de.eldoria.schematicbrush.brush.config.parameter;

import de.eldoria.schematicbrush.util.Randomable;

public enum Rotation implements Randomable {
    /**
     * Represents a rotation of 0.
     */
    ROT_ZERO(0),
    /**
     * Represents a rotation of 90 degrees counter clockwise.
     * Alterantive a rotation of -90 or 270 degrees.
     */
    ROT_LEFT(270),
    /**
     * Represents a rotation of 180 degrees.
     */
    ROT_HALF(180),
    /**
     * Represents a rotation of 90 degrees clockwise.
     * Alterantive a rotation of 90 degrees.
     */
    ROT_RIGHT(90),
    /**
     * Represents a random rotation.
     */
    ROT_RANDOM(-1);

    /**
     * Rotation represented as postive int value.
     * Can be null.
     */
    private final int deg;

    Rotation(int deg) {
        this.deg = deg;
    }

    /**
     * Rotation represented as postive int value.
     * Can be 0, 90, 180 and 270 if value is {@link #ROT_RANDOM}
     *
     * @return rotation as positive integer
     */
    public int getDeg() {
        if (deg == -1) {
            int degrees = RANDOM.nextInt(4);
            return degrees * 90;
        } else {
            return deg;
        }
    }

    public static Rotation asRotation(String s) {
        if ("0".equals(s)) return ROT_ZERO;
        if ("270".equals(s)) return ROT_LEFT;
        if ("90".equals(s)) return ROT_RIGHT;
        if ("180".equals(s)) return ROT_HALF;
        if ("*".equals(s)) return ROT_RANDOM;
        throw new IllegalArgumentException(s + " is not a value of Rotation");
    }
}
