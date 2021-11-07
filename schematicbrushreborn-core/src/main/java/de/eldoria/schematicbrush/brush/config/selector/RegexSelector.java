/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class RegexSelector extends BaseSelector {
    public RegexSelector(String term) {
        super("^" + term);
    }

    public RegexSelector(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public Set<Schematic> select(Player player, SchematicRegistry registry) {
        return registry.getCache(SchematicCache.DEFAULT_CACHE).getSchematicsByName(player, term());
    }

    @Override
    public String descriptor() {
        return term();
    }

    @Override
    public String name() {
        return "Regex";
    }
}
