/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

    private Colors() {
        throw new UnsupportedOperationException("This is a utility class.");
    }
}
