/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.modifier.BaseModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;

import java.util.List;

/**
 * Represents a registration in a {@link de.eldoria.schematicbrush.registry.Registry} or similar pattern.
 * @param <T> Type of modifier
 */
public interface Registration<T extends BaseModifier, V> {
    /**
     * List of registered Modifier providers
     * @return list of registered entries
     */
    V mutators();

    T modifier();
}
