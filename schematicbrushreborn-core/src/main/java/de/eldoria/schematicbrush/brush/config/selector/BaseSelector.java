/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public abstract class BaseSelector implements Selector {
    private final String term;

    public BaseSelector(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        term = map.getValue("term");
    }

    public BaseSelector(@Nullable String term) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseSelector baseSelector)) return false;

        return Objects.equals(term, baseSelector.term);
    }

    @Override
    public int hashCode() {
        return term != null ? term.hashCode() : 0;
    }
}
