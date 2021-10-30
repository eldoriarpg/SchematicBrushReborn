package de.eldoria.schematicbrush.util;

public final class Permissions {
    private static final String BASE = "schematicbrush";

    private static String perm(String perm, String... perms) {
        if (perms.length == 0) return perm;
        return String.format("%s.%s", perm, String.join(".", perms));
    }

    private Permissions() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static class Brush{
        private static final String BRUSH = perm(BASE, "brush");
        public static final String USE = perm(BRUSH, "use");
        public static final String PREVIEW = perm(BRUSH, "preview");
    }

    public static class Preset{
        private static final String PRESET = perm(BASE, "preset");
        public static final String USE = perm(PRESET, "use");
        public static final String GLOBAL = perm(PRESET, "global");
    }

    public static class Admin{
        private static final String ADMIN = perm(BASE, "admin");
        public static final String RELOAD = perm(ADMIN, "reload");
        public static final String RELOAD_CACHE = perm(ADMIN, "reloadcache");
        public static final String DEBUG = perm(ADMIN, "debug");
    }
}
