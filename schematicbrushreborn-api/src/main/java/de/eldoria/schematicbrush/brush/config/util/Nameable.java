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

package de.eldoria.schematicbrush.brush.config.util;

/**
 * Represents a named key.
 */
public class Nameable {
    private final String name;

    protected Nameable(String name) {
        this.name = name;
    }

    /**
     * Creates a new nameable with the name
     *
     * @param name name of nameable
     * @return new nameable instance
     */
    public static Nameable of(String name) {
        return new Nameable(name) {
        };
    }

    /**
     * name of the nameable
     *
     * @return name
     */
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nameable nameable)) return false;

        return name.equals(nameable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
