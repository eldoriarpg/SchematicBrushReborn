/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.eldoria.schematicbrush.schematics.Schematic;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

@SerializableAs("sbrRegexSelector")
public class RegexSelector extends BaseSelector {
    @JsonCreator
    public RegexSelector(@JsonProperty("term") String term) {
        super(term.startsWith("^") ? term : "^" + term);
    }

    public RegexSelector(Map<String, Object> objectMap) {
        super(objectMap);
    }

    @Override
    public Set<Schematic> select(Player player, SchematicRegistry registry) {
        return registry.get(SchematicCache.STORAGE).getSchematicsByName(player, term());
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
