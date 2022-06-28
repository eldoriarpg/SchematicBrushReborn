/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.modifier.BaseModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;

import java.util.List;

public interface Registration<T extends BaseModifier> {
    List<ModifierProvider> mutators();

    T modifier();
}
