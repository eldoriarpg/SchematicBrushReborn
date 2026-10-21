/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

/**
 * An expression that cannot be read, with the span of the expression at fault.
 */
public class ParseException extends Exception {
    private final int start;
    private final int end;

    /**
     * Creates an exception.
     *
     * @param message locale code or text explaining the problem
     * @param start   index of the first faulty character
     * @param end     index after the last faulty character
     */
    public ParseException(String message, int start, int end) {
        super(message);
        this.start = start;
        this.end = Math.max(end, start + 1);
    }

    /**
     * Creates an exception for a token.
     *
     * @param message locale code or text explaining the problem
     * @param token   faulty token
     */
    public ParseException(String message, Token token) {
        this(message, token.start(), token.end());
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }
}
