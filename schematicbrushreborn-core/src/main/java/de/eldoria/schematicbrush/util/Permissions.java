/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
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

    public static final class BrushPreset {
        private static final String BRUSHPRESET = perm(BASE, "brushpreset");
        public static final String USE = perm(BRUSHPRESET, "use");
        public static final String GLOBAL = perm(BRUSHPRESET, "global");

        private BrushPreset() {
            throw new UnsupportedOperationException("This is a utility class.");
        }
    }

    public static final class Admin {
        private static final String ADMIN = perm(BASE, "admin");
        public static final String RELOAD = perm(ADMIN, "reload");
        public static final String RESTART_RENDERING = perm(ADMIN, "restartrendering");
        public static final String RELOAD_CACHE = perm(ADMIN, "reloadcache");
        public static final String DEBUG = perm(ADMIN, "debug");
        public static final String MIGRATE = perm(ADMIN, "migrate");

        private Admin() {
            throw new UnsupportedOperationException("This is a utility class.");
        }
    }
}
