/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.legacy;

/**
 * Kinds of tokens in an expression of the original SchematicBrush.
 */
public enum TokenType {
    /**
     * Name, wildcard pattern or path of a schematic at the start of a definition.
     */
    SELECTOR,
    /**
     * Regular expression at the start of a definition, without the leading {@code ^}.
     */
    REGEX,
    /**
     * Preset reference, without the leading {@code &}.
     */
    PRESET,
    /**
     * Option name, without the leading {@code -}.
     */
    OPTION,
    /**
     * Raw value of an option, after the {@code :}.
     */
    VALUE,
    AT,
    COLON,
    CARET,
    HASH,
    COMMA,
    RANGE,
    STAR,
    NUMBER,
    LETTERS,
    /**
     * End of a whitespace separated item.
     */
    END
}
