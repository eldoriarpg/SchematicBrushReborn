package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.schematicbrush.brush.config.parameter.BrushSelector;
import de.eldoria.schematicbrush.brush.config.parameter.Flip;
import de.eldoria.schematicbrush.brush.config.parameter.Rotation;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BrushArgumentParser {
    private static final String NAME = ".+?)";
    private static final String ENDINGS = "(\\s|:|@|!|$)";

    /**
     * Pattern to use a name or a regex
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^(" + NAME + ENDINGS);

    /**
     * Pattern to use schematics inside a directory
     */
    private static final Pattern DIRECTORY_PATTERN = Pattern.compile("^\\$(" + NAME + ENDINGS);

    /**
     * Pattern to use a brush preset
     */
    private static final Pattern PRESET_PATTERN = Pattern.compile("^&(" + NAME + ENDINGS);

    /**
     * Pattern to detect the rotation
     */
    private static final Pattern ROTATION_PATTERN = Pattern.compile("@(0|90|180|270|\\*)" + ENDINGS);
    /**
     * Pattern to detect the flip
     */
    private static final Pattern FLIP_PATTERN = Pattern.compile("!(NS|SN|WE|EW|N|S|W|E|\\*)" + ENDINGS,
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern to detect the weight
     */
    private static final Pattern WEIGHT_PATTERN = Pattern.compile(":([0-9]{1,3}|\\*)" + ENDINGS);

    private BrushArgumentParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Get the type of the brush
     *
     * @param arguments arguments of the brush
     * @return optional brush type or empty if the brush could not be parsed.
     */
    public static Optional<SubBrushType> getBrushType(String arguments) {
        // Check if its a name or regex lookup

        // Check if its a directory lookup
        Matcher nameMatcher = DIRECTORY_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            String directoryName = nameMatcher.group(1);
            return Optional.of(new SubBrushType(BrushSelector.DIRECTORY, directoryName));
        }

        // Check if its a preset
        nameMatcher = PRESET_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            String presetName = nameMatcher.group(1);
            return Optional.of(new SubBrushType(BrushSelector.PRESET, presetName));

        }

        // Check if its a name or regex lookup
        nameMatcher = NAME_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            String pattern = nameMatcher.group(1);
            return Optional.of(new SubBrushType(BrushSelector.REGEX, pattern));
        }
        return Optional.empty();
    }

    /**
     * Parse the values of a brush to a {@link SubBrushValues} object.
     * Values will be null, if the could not be found in the arguments.
     *
     * @param arguments arguments of brush
     * @return values wrapped in a object.
     */
    public static SubBrushValues getBrushValues(String arguments) {
        @Nullable Flip flip = null;
        @Nullable Rotation rotation = null;
        @Nullable Integer weight = null;

        // Read rotation
        Matcher matcher = ROTATION_PATTERN.matcher(arguments);
        if (matcher.find()) {
            String value = matcher.group(1);
            rotation = Rotation.asRotation(value);
        }

        // Read flip
        matcher = FLIP_PATTERN.matcher(arguments);
        if (matcher.find()) {
            String value = matcher.group(1);
            flip = Flip.asFlip(value);
        }

        // Read weight
        matcher = WEIGHT_PATTERN.matcher(arguments);
        if (matcher.find()) {
            String value = matcher.group(1);
            if ("*".equals(value)) {
                weight = -1;
            } else {
                weight = Integer.parseInt(value);
                if (weight < 0) {
                    weight = null;
                }
            }
        }
        return new SubBrushValues(flip, rotation, weight);
    }

    /**
     * This class represents the values of a sub brush.
     * Values which are not present are null.
     */
    @Data
    public static final class SubBrushValues {
        /**
         * Flip of the brush.
         */
        @Nullable
        private final Flip flip;
        /**
         * Rotation of the brush.
         */
        @Nullable
        private final Rotation rotation;
        /**
         * Weight of the brush
         */
        @Nullable
        private final Integer weight;

        public SubBrushValues(@Nullable Flip flip, @Nullable Rotation rotation, @Nullable Integer weight) {
            this.flip = flip;
            this.rotation = rotation;
            this.weight = weight;
        }
    }

    /**
     * This class represents the type of a brush.
     */
    @Data
    public static final class SubBrushType {
        /**
         * Selector type of the brush.
         */
        @Nonnull
        private final BrushSelector selectorType;
        /**
         * Value of the selector.
         */
        @Nonnull
        private final String selectorValue;

        public SubBrushType(@Nonnull BrushSelector selectorType, @Nonnull String selectorValue) {
            this.selectorType = selectorType;
            this.selectorValue = selectorValue;
        }
    }
}
