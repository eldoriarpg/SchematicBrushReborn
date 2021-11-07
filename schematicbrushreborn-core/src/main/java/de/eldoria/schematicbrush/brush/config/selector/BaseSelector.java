/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class BaseSelector implements Selector {
    private final String term;

    public BaseSelector(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        term = map.getValue("term");
    }

    public BaseSelector(String term) {
        this.term = term;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("term", term)
                .build();
    }

    public String term() {
        return term;
    }

    @Override
    public String descriptor() {
        return term;
    }
}
