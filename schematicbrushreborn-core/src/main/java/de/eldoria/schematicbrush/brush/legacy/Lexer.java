/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

import java.util.ArrayList;
import java.util.List;

import static de.eldoria.schematicbrush.brush.legacy.TokenType.AT;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.CARET;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.COLON;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.COMMA;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.END;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.HASH;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.LETTERS;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.NUMBER;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.OPTION;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.PRESET;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.RANGE;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.REGEX;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.SELECTOR;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.STAR;
import static de.eldoria.schematicbrush.brush.legacy.TokenType.VALUE;

/**
 * Splits an expression into tokens.
 * <p>
 * Items are separated by whitespace and each ends with an {@link TokenType#END} token. An item starting with {@code &}
 * is a preset, one starting with {@code -} and a letter is an option with an optional raw value after {@code :}.
 * Anything else is a definition: a selector up to the first of {@code @ : ^ #} (a leading {@code ^} marks a regex,
 * which then runs up to the first of {@code @ : #}), followed by its parts.
 */
public final class Lexer {
    private static final String PART_START = "@:^#";
    private static final String REGEX_END = "@:#";

    private final String input;
    private final int base;
    private final List<Token> tokens = new ArrayList<>();
    private int pos;

    private Lexer(String input, int base) {
        this.input = input;
        this.base = base;
    }

    /**
     * Tokenizes a whole expression.
     *
     * @param input expression
     * @return tokens
     * @throws ParseException if a character cannot start a token
     */
    public static List<Token> tokenize(String input) throws ParseException {
        var lexer = new Lexer(input, 0);
        lexer.items();
        return lexer.tokens;
    }

    /**
     * Tokenizes the value of an option the way the parts of a definition are tokenized.
     *
     * @param value value token of an option
     * @return tokens, ending with {@link TokenType#END}
     * @throws ParseException if a character cannot start a token
     */
    public static List<Token> parts(Token value) throws ParseException {
        var lexer = new Lexer(value.text(), value.start());
        lexer.parts(value.text().length());
        lexer.add(END, "", lexer.pos, lexer.pos);
        return lexer.tokens;
    }

    private void items() throws ParseException {
        while (true) {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) pos++;
            if (pos >= input.length()) return;
            var end = pos;
            while (end < input.length() && !Character.isWhitespace(input.charAt(end))) end++;
            item(end);
            add(END, "", end, end);
            pos = end;
        }
    }

    private void item(int end) throws ParseException {
        var first = input.charAt(pos);
        if (first == '&') {
            add(PRESET, input.substring(pos + 1, end), pos, end);
            return;
        }
        if (first == '-' && pos + 1 < end && Character.isLetter(input.charAt(pos + 1))) {
            var colon = input.indexOf(':', pos);
            var nameEnd = colon == -1 || colon > end ? end : colon;
            add(OPTION, input.substring(pos + 1, nameEnd), pos, nameEnd);
            if (nameEnd < end) add(VALUE, input.substring(nameEnd + 1, end), nameEnd + 1, end);
            return;
        }
        var regex = first == '^';
        var stops = regex ? REGEX_END : PART_START;
        var from = regex ? pos + 1 : pos;
        var selectorEnd = from;
        while (selectorEnd < end && stops.indexOf(input.charAt(selectorEnd)) < 0) selectorEnd++;
        add(regex ? REGEX : SELECTOR, input.substring(from, selectorEnd), pos, selectorEnd);
        pos = selectorEnd;
        parts(end);
    }

    private void parts(int end) throws ParseException {
        while (pos < end) {
            var current = input.charAt(pos);
            switch (current) {
                case '@' -> symbol(AT);
                case ':' -> symbol(COLON);
                case '^' -> symbol(CARET);
                case '#' -> symbol(HASH);
                case ',' -> symbol(COMMA);
                case '*' -> symbol(STAR);
                case '.' -> {
                    if (pos + 1 >= end || input.charAt(pos + 1) != '.') throw unexpected();
                    add(RANGE, "..", pos, pos + 2);
                    pos += 2;
                }
                default -> {
                    if (Character.isDigit(current) || current == '-' && pos + 1 < end && Character.isDigit(input.charAt(pos + 1))) {
                        run(NUMBER, end, pos + 1, Character::isDigit);
                    } else if (Character.isLetter(current)) {
                        run(LETTERS, end, pos, Character::isLetter);
                    } else {
                        throw unexpected();
                    }
                }
            }
        }
    }

    private void run(TokenType type, int end, int from, CharTest test) {
        var runEnd = from;
        while (runEnd < end && test.matches(input.charAt(runEnd))) runEnd++;
        add(type, input.substring(pos, runEnd), pos, runEnd);
        pos = runEnd;
    }

    private void symbol(TokenType type) {
        add(type, String.valueOf(input.charAt(pos)), pos, pos + 1);
        pos++;
    }

    private void add(TokenType type, String text, int start, int end) {
        tokens.add(new Token(type, text, base + start, base + end));
    }

    private ParseException unexpected() {
        return new ParseException("commands.brush.parse.error.character", base + pos, base + pos + 1);
    }

    @FunctionalInterface
    private interface CharTest {
        boolean matches(char value);
    }
}
