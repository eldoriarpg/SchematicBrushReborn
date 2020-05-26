package de.eldoria.schematicbrush.commands.parser;

import de.eldoria.schematicbrush.brush.BrushSelector;
import de.eldoria.schematicbrush.util.Flip;
import de.eldoria.schematicbrush.util.Rotation;
import lombok.Data;
import lombok.experimental.UtilityClass;

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
     * @param arguments arguments of the brush
     * @return optional brush type or empty if the brush could not be parsed.
     */
    public Optional<BrushType> getBrushType(String arguments) {
        // Check if its a name or regex lookup
        Matcher nameMatcher = NAME_PATTERN.matcher(arguments);
        // Check if its a name or regex lookup
        if (nameMatcher.find()) {
            String pattern = nameMatcher.group(1);
            return Optional.of(new BrushType(BrushSelector.REGEX, pattern));
        }

        // Check if its a directory lookup
        nameMatcher = DIRECTORY_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            String directoryName = nameMatcher.group(1);
            return Optional.of(new BrushType(BrushSelector.DIRECTORY, directoryName));
        }

        // Check if its a preset
        nameMatcher = PRESET_PATTERN.matcher(arguments);
        if (nameMatcher.find()) {
            String presetName = nameMatcher.group(1);
            return Optional.of(new BrushType(BrushSelector.PRESET, presetName));
        }
        return Optional.empty();
    }

    public BrushConfigValues getBrushValues(String arguments) {
        Flip flip = null;
        Rotation rotation = null;
        Integer weight = null;

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
        return new BrushConfigValues(flip, rotation, weight);
    }

    @Data
    public class BrushConfigValues {
        private final Flip flip;
        private final Rotation rotation;
        private final Integer weight;
    }

    @Data
    public class BrushType {
        private final BrushSelector selectorType;
        private final String selectorValue;
    }
}
