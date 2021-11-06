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

public final class Permissions {
    private static final String BASE = "schematicbrush";

    private Permissions() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    private static String perm(String... perms) {
        return String.join(".", perms);
    }

    public static final class Brush {
        private static final String BRUSH = perm(BASE, "brush");
        public static final String USE = perm(BRUSH, "use");
        public static final String PREVIEW = perm(BRUSH, "preview");

        private Brush() {
            throw new UnsupportedOperationException("This is a utility class.");
        }
    }

    public static final class Preset {
        private static final String PRESET = perm(BASE, "preset");
        public static final String USE = perm(PRESET, "use");
        public static final String GLOBAL = perm(PRESET, "global");

        private Preset() {
            throw new UnsupportedOperationException("This is a utility class.");
        }
    }

    public static final class Admin {
        private static final String ADMIN = perm(BASE, "admin");
        public static final String RELOAD = perm(ADMIN, "reload");
        public static final String RELOAD_CACHE = perm(ADMIN, "reloadcache");
        public static final String DEBUG = perm(ADMIN, "debug");

        private Admin() {
            throw new UnsupportedOperationException("This is a utility class.");
        }
    }
}
