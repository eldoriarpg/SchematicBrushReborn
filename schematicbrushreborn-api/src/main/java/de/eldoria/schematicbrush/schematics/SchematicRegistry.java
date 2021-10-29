package de.eldoria.schematicbrush.schematics;

import de.eldoria.schematicbrush.brush.config.util.Nameable;

import java.util.HashMap;
import java.util.Map;

public class SchematicRegistry {
    private final Map<Nameable, SchematicCache> caches = new HashMap<>();

    public SchematicCache getCache(Nameable key) {
        return caches.get(key);
    }

    public void register(Nameable key, SchematicCache cache) {
        cache.init();
        caches.put(key, cache);
    }

    public void unregister(Nameable key) {
        caches.remove(key);
    }

    public void reload() {
        caches.values().forEach(SchematicCache::reload);
    }

    public int schematicCount() {
        return caches.values().stream().mapToInt(SchematicCache::schematicCount).sum();
    }

    public int directoryCount() {
        return caches.values().stream().mapToInt(SchematicCache::directoryCount).sum();
    }
}
