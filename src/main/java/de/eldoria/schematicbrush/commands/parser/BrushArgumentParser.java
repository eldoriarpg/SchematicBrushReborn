package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.schematicbrush.brush.BrushSelector;
import de.eldoria.schematicbrush.util.Flip;
import de.eldoria.schematicbrush.util.Rotation;
import lombok.Data;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class BrushArgumentParser {
    private final String NAME = ".+?)";
    private final String ENDINGS = "(\\s|:|@|!|$)";

    /**
     * Pattern to use a name or a regex
     */
    private final Pattern NAME_PATTERN = Pattern.compile("^(" + NAME + ENDINGS);

    /**
     * Pattern to use schematics inside a directory
     */
    private final Pattern DIRECTORY_PATTERN = Pattern.compile("^(\\$" + NAME + ENDINGS);

    /**
     * Pattern to use a brush preset
     */
    private final Pattern PRESET_PATTERN = Pattern.compile("^&(" + NAME + ENDINGS);

    /**
     * Pattern to detect the rotation
     */
    private final Pattern ROTATION_PATTERN = Pattern.compile("@(0|90|180|270|\\*)" + ENDINGS);
    /**
     * Pattern to detect the flip
     */
    private final Pattern FLIP_PATTERN = Pattern.compile("!(NS|SN|WO|OW|N|S|W|E|\\*)" + ENDINGS, Pattern.CASE_INSENSITIVE);
    /**
     * Pattern to detect the weight
     */
    private final Pattern WEIGHT_PATTERN = Pattern.compile(":([0-9]{1,3}|\\*)" + ENDINGS);

    /**
     * Get the type of the brush
     *
     * @param arguments arguments of the brush
     * @return optional brush type or empty if the brush could not be parsed.
     */
    public Optional<SubBrushType> getBrushType(String arguments) {
        // Check if its a name or regex lookup
        Matcher nameMatcher = NAME_PATTERN.matcher(arguments);
        // Check if its a name or regex lookup
        if (nameMatcher.find()) {
            String pattern = nameMatcher.group(1);
            return Optional.of(new SubBrushType(BrushSelector.REGEX, pattern));
        }

        // Check if its a directory lookup
        nameMatcher = DIRECTORY_PATTERN.matcher(arguments);
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
        return Optional.empty();
    }

    /**
     * Parse the values of a brush to a {@link SubBrushValues} object.
     * Values will be null, if the could not be found in the arguments.
     *
     * @param arguments arguments of brush
     * @return values wrapped in a object.
     */
    public SubBrushValues getBrushValues(String arguments) {
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
    public class SubBrushValues {
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
    }

    /**
     * This class represents the type of a brush.
     */
    @Data
    public class SubBrushType {
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
    }
}
