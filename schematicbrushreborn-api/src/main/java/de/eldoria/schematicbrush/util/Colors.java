/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.util;

import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Class holding some colors sorted by category
 */
public final class Colors {
    public static final String HEADING = NamedTextColor.GOLD.toString();
    public static final String NAME = NamedTextColor.DARK_AQUA.toString();
    public static final String VALUE = NamedTextColor.DARK_GREEN.toString();
    public static final String CHANGE = NamedTextColor.YELLOW.toString();
    public static final String REMOVE = NamedTextColor.RED.toString();
    public static final String ADD = NamedTextColor.GREEN.toString();
    public static final String NEUTRAL = NamedTextColor.AQUA.toString();
    public static final String INACTIVE = NamedTextColor.GRAY.toString();

    private Colors() {
        throw new UnsupportedOperationException("This is a utility class.");
    }
}
