/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

/**
 * A token of an expression.
 *
 * @param type  kind of token
 * @param text  text of the token, without markers like {@code &} or {@code -}
 * @param start index of the first character in the expression
 * @param end   index after the last character in the expression
 */
public record Token(TokenType type, String text, int start, int end) {
}
