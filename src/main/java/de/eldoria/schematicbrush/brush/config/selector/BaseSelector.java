package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.SchematicCache;

public abstract class BaseSelector implements Selector{
    private final String term;
    private final SchematicCache cache;

    public BaseSelector(String term, SchematicCache cache) {
        this.term = term;
        this.cache = cache;
    }

    public String term() {
        return term;
    }

    public SchematicCache cache() {
        return cache;
    }
}
