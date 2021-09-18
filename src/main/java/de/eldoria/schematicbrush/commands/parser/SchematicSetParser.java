package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.schematicbrush.brush.config.flip.AFlip;
import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.config.parameter.SchematicSelector;
import de.eldoria.schematicbrush.brush.config.rotation.ARotation;
import de.eldoria.schematicbrush.brush.config.rotation.Rotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SchematicSetParser {
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
    private static final Pattern ROTATION_PATTERN = Pattern.compile("@(\\[?((?:(?:0|90|180|270|\\*),?)+?)]?)" + ENDINGS);
    /**
     * Pattern to detect the flip
     */
    private static final Pattern FLIP_PATTERN = Pattern.compile("!(\\[?((?:(?:NS|SN|WE|EW|N|S|W|E|\\*),?)+?)]?)" + ENDINGS,
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern to detect the weight
     */
    private static final Pattern WEIGHT_PATTERN = Pattern.compile(":([0-9]{1,3}|\\*)" + ENDINGS);

    private SchematicSetParser() {
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
        var nameMatcher = DIRECTORY_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            var directoryName = nameMatcher.group(1);
            return Optional.of(new SubBrushType(SchematicSelector.DIRECTORY, directoryName));
        }

        // Check if its a preset
        nameMatcher = PRESET_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            var presetName = nameMatcher.group(1);
            return Optional.of(new SubBrushType(SchematicSelector.PRESET, presetName));

        }

        // Check if its a name or regex lookup
        nameMatcher = NAME_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            var pattern = nameMatcher.group(1);
            return Optional.of(new SubBrushType(SchematicSelector.REGEX, pattern));
        }
        return Optional.empty();
    }

    /**
     * Parse the values of a brush to a {@link SubBrushValues} object. Values will be null, if the could not be found in
     * the arguments.
     *
     * @param arguments arguments of brush
     * @return values wrapped in a object.
     */
    public static SubBrushValues getBrushValues(String arguments) {
        @Nullable AFlip flip = null;
        @Nullable ARotation rotation = null;
        @Nullable Integer weight = null;

        // Read rotation
        var matcher = ROTATION_PATTERN.matcher(arguments);
        if (matcher.find()) {
            var value = matcher.group(1);
            var parseResult = ParsingUtil.parseValue(value, r -> Optional.of(Rotation.asRotation(r)));
            switch (parseResult.type()) {
                case NONE:
                case RANGE:
                    break;
                case LIST:
                    rotation = ARotation.list(parseResult.results());
                    break;
                case RANDOM:
                    rotation = ARotation.random();
                    break;
                case FIXED:
                    rotation = ARotation.fixed(parseResult.result());
                    break;
            }
        }

        // Read flip
        matcher = FLIP_PATTERN.matcher(arguments);
        if (matcher.find()) {
            var value = matcher.group(1);
            var parseResult = ParsingUtil.parseValue(value, r -> Optional.of(Flip.asFlip(r)));
            switch (parseResult.type()) {
                case NONE:
                case RANGE:
                    break;
                case LIST:
                    flip = AFlip.list(parseResult.results());
                    break;
                case RANDOM:
                    flip = AFlip.random();
                    break;
                case FIXED:
                    flip = AFlip.fixed(parseResult.result());
                    break;
            }
        }

        // Read weight
        matcher = WEIGHT_PATTERN.matcher(arguments);
        if (matcher.find()) {
            var value = matcher.group(1);
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
     * This class represents the values of a sub brush. Values which are not present are null.
     */
    public static final class SubBrushValues {
        /**
         * Flip of the brush.
         */
        @Nullable
        private final AFlip flip;
        /**
         * Rotation of the brush.
         */
        @Nullable
        private final ARotation rotation;
        /**
         * Weight of the brush
         */
        @Nullable
        private final Integer weight;

        public SubBrushValues(@Nullable AFlip flip, @Nullable ARotation rotation, @Nullable Integer weight) {
            this.flip = flip;
            this.rotation = rotation;
            this.weight = weight;
        }

        @Nullable
        public AFlip flip() {
            return flip;
        }

        @Nullable
        public ARotation rotation() {
            return rotation;
        }

        @Nullable
        public Integer weight() {
            return weight;
        }
    }

    /**
     * This class represents the type of a brush.
     */
    public static final class SubBrushType {
        /**
         * Selector type of the brush.
         */
        @Nonnull
        private final SchematicSelector selectorType;
        /**
         * Value of the selector.
         */
        @Nonnull
        private final String selectorValue;

        public SubBrushType(@Nonnull SchematicSelector selectorType, @Nonnull String selectorValue) {
            this.selectorType = selectorType;
            this.selectorValue = selectorValue;
        }

        @Nonnull
        public SchematicSelector selectorType() {
            return selectorType;
        }

        @Nonnull
        public String selectorValue() {
            return selectorValue;
        }
    }
}
