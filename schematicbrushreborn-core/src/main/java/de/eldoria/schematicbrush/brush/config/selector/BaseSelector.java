/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.selector;

import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.schematicbrush.util.Escape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class BaseSelector implements Selector {
    private final String term;

    public BaseSelector(@Nullable String term) {
        this.term = term;
    }

    public String term() {
        return term;
    }

    @Override
    public String descriptor() {
        return Escape.text(term);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseSelector baseSelector)) return false;

        return Objects.equals(term, baseSelector.term);
    }

    @Override
    public String localizedName() {
        return ILocalizer.escape("components.selector.%s.name".formatted(name().toLowerCase()));
    }

    @Override
    public int hashCode() {
        return term != null ? term.hashCode() : 0;
    }
}
