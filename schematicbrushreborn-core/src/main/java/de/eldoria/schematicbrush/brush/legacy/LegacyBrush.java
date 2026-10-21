/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import de.eldoria.schematicbrush.brush.config.flip.Flip;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A brush read from an expression of the original SchematicBrush, before it is mapped onto Schematic Brush.
 *
 * @param definitions schematic definitions, one set each
 * @param presets     presets whose sets are added
 * @param options     options of the whole brush
 */
public record LegacyBrush(List<Definition> definitions, List<Token> presets, Options options) {

    /**
     * One weighted entry of the brush.
     *
     * @param selector name, pattern or path of the schematics
     * @param regex    whether the selector is a regular expression
     * @param rotation rotation, null if none was given
     * @param flip     flip, null if none was given
     * @param weight   weight from 1 to 100, null for automatic
     * @param offset   offset, null if none was given
     * @param format   file format given after {@code #}, which is ignored; null if none was given
     */
    public record Definition(Token selector, boolean regex, @Nullable RotationSpec rotation, @Nullable FlipSpec flip,
                             @Nullable Integer weight, @Nullable OffsetSpec offset, @Nullable String format) {
    }

    /**
     * Options of the whole brush.
     *
     * @param placement  placement, null for the default
     * @param includeAir whether air of the schematic is pasted
     * @param replaceAll whether existing blocks are replaced
     * @param offset     offset of the whole brush, null if none was given
     * @param rotation   rotation of the whole brush, null if none was given
     * @param flip       flip of the whole brush, null if none was given
     * @param filter     WorldEdit mask the paste is limited to, null if none was given
     */
    public record Options(@Nullable Placement placement, boolean includeAir, boolean replaceAll,
                          @Nullable OffsetSpec offset, @Nullable RotationSpec rotation, @Nullable FlipSpec flip,
                          @Nullable Token filter) {
    }

    /**
     * A rotation: random, or chosen from the listed degrees.
     */
    public sealed interface RotationSpec {
        record Random() implements RotationSpec {
        }

        record Degrees(List<Integer> degrees) implements RotationSpec {
        }
    }

    /**
     * A flip: random among none, north–south and east–west like the original, or chosen from the listed axes.
     */
    public sealed interface FlipSpec {
        record Random() implements FlipSpec {
        }

        record Axes(List<Flip> axes) implements FlipSpec {
        }
    }

    /**
     * An offset: fixed, a range, or chosen from the listed values.
     */
    public sealed interface OffsetSpec {
        record Fixed(int value) implements OffsetSpec {
        }

        record Range(int min, int max) implements OffsetSpec {
        }

        record Values(List<Integer> values) implements OffsetSpec {
        }
    }

    /**
     * Placements by the names the option accepts. {@code center} is the name the original used for the middle.
     */
    public enum Placement {
        MIDDLE("center", "middle"), BOTTOM("bottom"), DROP("drop"), RAISE("raise"), TOP("top"), ORIGINAL("original");

        private final List<String> names;

        Placement(String... names) {
            this.names = List.of(names);
        }

        /**
         * Names accepted for this placement.
         *
         * @return lower case names
         */
        public List<String> names() {
            return names;
        }

        /**
         * Finds a placement by one of its names, ignoring case.
         *
         * @param name name
         * @return placement, empty if the name is unknown
         */
        public static Optional<Placement> byName(String name) {
            var lower = name.toLowerCase(Locale.ROOT);
            for (var placement : values()) {
                if (placement.names.contains(lower)) return Optional.of(placement);
            }
            return Optional.empty();
        }
    }
}
