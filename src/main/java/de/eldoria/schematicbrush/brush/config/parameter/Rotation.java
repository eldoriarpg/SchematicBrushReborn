package de.eldoria.schematicbrush.brush.config.parameter;

import de.eldoria.schematicbrush.util.Randomable;

public enum Rotation implements Randomable {
    /**
     * Represents a rotation of 0.
     */
    ROT_ZERO(0),
    /**
     * Represents a rotation of 90 degrees counter clockwise. Alterantive a rotation of -90 or 270 degrees.
     */
    ROT_LEFT(270),
    /**
     * Represents a rotation of 180 degrees.
     */
    ROT_HALF(180),
    /**
     * Represents a rotation of 90 degrees clockwise. Alterantive a rotation of 90 degrees.
     */
    ROT_RIGHT(90),
    /**
     * Represents a random rotation.
     */
    ROT_RANDOM(-1);

    /**
     * Rotation represented as postive int value. Can be null.
     */
    private final int deg;

    Rotation(int deg) {
        this.deg = deg;
    }

    /**
     * Get a string as rotation value.
     *
     * @param value value to parse
     *
     * @return rotation enum
     *
     * @throws IllegalArgumentException when value cant be parsed
     */
    public static Rotation asRotation(String value) {
        if ("0".equals(value)) return ROT_ZERO;
        if ("270".equals(value)) return ROT_LEFT;
        if ("90".equals(value)) return ROT_RIGHT;
        if ("180".equals(value)) return ROT_HALF;
        if ("*".equals(value)) return ROT_RANDOM;
        throw new IllegalArgumentException(value + " is not a value of Rotation");
    }

    /**
     * Rotation represented as postive int value. Can be 0, 90, 180 and 270 if value is {@link #ROT_RANDOM}.
     *
     * @return rotation as positive integer
     */
    public int getDeg() {
        if (deg == -1) {
            int degrees = random().nextInt(4);
            return degrees * 90;
        } else {
            return deg;
        }
    }
}
