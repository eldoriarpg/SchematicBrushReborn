/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Escapes text entered by players before it is placed into a MiniMessage string.
 */
public final class Escape {
    private Escape() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    /**
     * Escapes tags, so the text is shown as written.
     *
     * @param text text entered by a player
     * @return text that renders literally in MiniMessage
     */
    public static String text(String text) {
        return MiniMessage.miniMessage().escapeTags(text);
    }

    /**
     * Escapes quotes and backslashes, so the text stays inside a single-quoted tag argument such as a click command
     * or hover text.
     *
     * @param argument argument content
     * @return content that can be placed between single quotes of a tag argument
     */
    public static String argument(String argument) {
        return argument.replace("\\", "\\\\").replace("'", "\\'");
    }
}
