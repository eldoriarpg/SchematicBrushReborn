/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import de.eldoria.schematicbrush.brush.config.flip.Flip;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Definition;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.FlipSpec;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.OffsetSpec;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Options;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.Placement;
import de.eldoria.schematicbrush.brush.legacy.LegacyBrush.RotationSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import static de.eldoria.schematicbrush.brush.legacy.TokenType.COMMA;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.END;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.LETTERS;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.NUMBER;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.RANGE;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.STAR;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.VALUE;

/**
 * Reads an expression of the original SchematicBrush, extended by what Schematic Brush can do.
 * <pre>
 * expression = item { item }
 * item       = definition | "&amp;" preset | option
 * definition = selector { "@" rotation [ flip ] | "@" flip | ":" weight | "^" offset | "#" format }
 * rotation   = "*" | degrees { "," degrees }
 * flip       = "*" | letters of N S E W U D
 * offset     = int | int ".." int | int { "," int }
 * option     = -incair | -replaceall | -place:placement | -yoff:offset | -rot:rotation | -flip:flip | -filter:mask
 * </pre>
 * Every part of a definition and every option may be given once.
 */
public final class Parser {
    private static final int MAX_OFFSET = 100;
    private static final int MAX_WEIGHT = 100;

    private final List<Token> tokens;
    private int index;

    private Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses an expression.
     *
     * @param input expression
     * @return the brush it describes
     * @throws ParseException if the expression is invalid
     */
    public static LegacyBrush parse(String input) throws ParseException {
        return new Parser(Lexer.tokenize(input)).expression(input);
    }

    private LegacyBrush expression(String input) throws ParseException {
        var definitions = new ArrayList<Definition>();
        var presets = new ArrayList<Token>();
        var options = new OptionsBuilder();
        while (index < tokens.size()) {
            var token = next();
            switch (token.type()) {
                case PRESET -> {
                    if (token.text().isEmpty()) throw new ParseException("commands.brush.parse.error.emptyPreset", token);
                    presets.add(token);
                }
                case OPTION -> option(token, options);
                case SELECTOR, REGEX -> definitions.add(definition(token));
                default -> throw unexpected(token);
            }
            expect(END);
        }
        if (definitions.isEmpty() && presets.isEmpty()) {
            throw new ParseException("commands.brush.parse.error.empty", 0, input.length());
        }
        return new LegacyBrush(definitions, presets, options.build());
    }

    private Definition definition(Token selector) throws ParseException {
        if (selector.text().isEmpty()) throw new ParseException("commands.brush.parse.error.emptyName", selector);
        RotationSpec rotation = null;
        FlipSpec flip = null;
        Integer weight = null;
        OffsetSpec offset = null;
        String format = null;
        var orientation = false;
        while (peek().type() != END) {
            var token = next();
            switch (token.type()) {
                case AT -> {
                    if (orientation) throw duplicate(token);
                    orientation = true;
                    if (peek().type() == STAR || peek().type() == NUMBER) rotation = rotation();
                    if (peek().type() == STAR || peek().type() == LETTERS) flip = flip();
                    if (rotation == null && flip == null) throw new ParseException("commands.brush.parse.error.rotationOrFlip", peek());
                }
                case COLON -> {
                    if (weight != null) throw duplicate(token);
                    var value = expect(NUMBER);
                    weight = bounded(value, 1, MAX_WEIGHT, "commands.brush.parse.error.weight");
                }
                case CARET -> {
                    if (offset != null) throw duplicate(token);
                    offset = offset();
                }
                case HASH -> {
                    if (format != null) throw duplicate(token);
                    format = format();
                }
                default -> throw unexpected(token);
            }
        }
        return new Definition(selector, selector.type() == TokenType.REGEX, rotation, flip, weight, offset, format);
    }

    private RotationSpec rotation() throws ParseException {
        if (peek().type() == STAR) {
            next();
            return new RotationSpec.Random();
        }
        var degrees = new ArrayList<Integer>();
        do {
            var token = expect(NUMBER);
            var value = number(token);
            if (value < 0 || value % 90 != 0) throw new ParseException("commands.brush.parse.error.rotation", token);
            degrees.add(value % 360);
        } while (accept(COMMA));
        return new RotationSpec.Degrees(List.copyOf(new LinkedHashSet<>(degrees)));
    }

    private FlipSpec flip() throws ParseException {
        if (peek().type() == STAR) {
            next();
            return new FlipSpec.Random();
        }
        var token = expect(LETTERS);
        var axes = new LinkedHashSet<Flip>();
        for (var offset = 0; offset < token.text().length(); offset++) {
            var axis = switch (Character.toUpperCase(token.text().charAt(offset))) {
                case 'N', 'S' -> Flip.NORTH_SOUTH;
                case 'E', 'W' -> Flip.EAST_WEST;
                case 'U', 'D' -> Flip.UP_DOWN;
                default -> throw new ParseException("commands.brush.parse.error.flip", token.start() + offset, token.start() + offset + 1);
            };
            axes.add(axis);
        }
        return new FlipSpec.Axes(List.copyOf(axes));
    }

    private OffsetSpec offset() throws ParseException {
        var first = offsetValue(expect(NUMBER));
        if (accept(RANGE)) {
            var second = offsetValue(expect(NUMBER));
            return new OffsetSpec.Range(Math.min(first, second), Math.max(first, second));
        }
        if (peek().type() != COMMA) return new OffsetSpec.Fixed(first);
        var values = new ArrayList<Integer>();
        values.add(first);
        while (accept(COMMA)) values.add(offsetValue(expect(NUMBER)));
        return new OffsetSpec.Values(values);
    }

    private String format() throws ParseException {
        var format = new StringBuilder();
        while (peek().type() == LETTERS || peek().type() == NUMBER) format.append(next().text());
        if (format.isEmpty()) throw new ParseException("commands.brush.parse.error.format", peek());
        return format.toString();
    }

    private void option(Token name, OptionsBuilder options) throws ParseException {
        var value = peek().type() == VALUE ? next() : null;
        switch (name.text().toLowerCase(Locale.ROOT)) {
            case "incair" -> {
                noValue(value);
                if (options.includeAir) throw duplicate(name);
                options.includeAir = true;
            }
            case "replaceall" -> {
                noValue(value);
                if (options.replaceAll) throw duplicate(name);
                options.replaceAll = true;
            }
            case "place" -> {
                if (options.placement != null) throw duplicate(name);
                var raw = value(name, value);
                options.placement = Placement.byName(raw.text())
                                             .orElseThrow(() -> new ParseException("commands.brush.parse.error.placement", raw));
            }
            case "yoff" -> {
                if (options.offset != null) throw duplicate(name);
                options.offset = sub(value(name, value)).offset();
            }
            case "rot" -> {
                if (options.rotation != null) throw duplicate(name);
                var parser = sub(value(name, value));
                if (parser.peek().type() != STAR && parser.peek().type() != NUMBER) {
                    throw new ParseException("commands.brush.parse.error.rotation", parser.peek());
                }
                options.rotation = parser.rotation();
                parser.expect(END);
            }
            case "flip" -> {
                if (options.flip != null) throw duplicate(name);
                var parser = sub(value(name, value));
                options.flip = parser.flip();
                parser.expect(END);
            }
            case "filter" -> {
                if (options.filter != null) throw duplicate(name);
                options.filter = value(name, value);
            }
            default -> throw new ParseException("commands.brush.parse.error.option", name);
        }
    }

    private static Parser sub(Token value) throws ParseException {
        return new Parser(Lexer.parts(value));
    }

    private static Token value(Token name, @Nullable Token value) throws ParseException {
        if (value == null || value.text().isEmpty()) throw new ParseException("commands.brush.parse.error.optionValue", name);
        return value;
    }

    private static void noValue(@Nullable Token value) throws ParseException {
        if (value != null) throw new ParseException("commands.brush.parse.error.noValue", value);
    }

    private int offsetValue(Token token) throws ParseException {
        return bounded(token, -MAX_OFFSET, MAX_OFFSET, "commands.brush.parse.error.offset");
    }

    private static int bounded(Token token, int min, int max, String message) throws ParseException {
        var value = number(token);
        if (value < min || value > max) throw new ParseException(message, token);
        return value;
    }

    private static int number(Token token) throws ParseException {
        try {
            return Integer.parseInt(token.text());
        } catch (NumberFormatException e) {
            throw new ParseException("commands.brush.parse.error.number", token);
        }
    }

    private Token peek() {
        return tokens.get(index);
    }

    private Token next() {
        return tokens.get(index++);
    }

    private boolean accept(TokenType type) {
        if (peek().type() != type) return false;
        index++;
        return true;
    }

    private Token expect(TokenType type) throws ParseException {
        var token = peek();
        if (token.type() != type) {
            throw new ParseException(type == NUMBER ? "commands.brush.parse.error.number" : "commands.brush.parse.error.unexpected", token);
        }
        index++;
        return token;
    }

    private static ParseException unexpected(Token token) {
        return new ParseException("commands.brush.parse.error.unexpected", token);
    }

    private static ParseException duplicate(Token token) {
        return new ParseException("commands.brush.parse.error.duplicate", token);
    }

    /**
     * Collects the options while they are read.
     */
    private static final class OptionsBuilder {
        private Placement placement;
        private boolean includeAir;
        private boolean replaceAll;
        private OffsetSpec offset;
        private RotationSpec rotation;
        private FlipSpec flip;
        private Token filter;

        private Options build() {
            return new Options(placement, includeAir, replaceAll, offset, rotation, flip, filter);
        }
    }
}
