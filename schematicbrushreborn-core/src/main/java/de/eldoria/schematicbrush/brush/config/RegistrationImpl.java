/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.modifier.BaseModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;

import java.util.Collections;
import java.util.List;

public class RegistrationImpl<T extends BaseModifier> implements Registration<T, List<ModifierProvider>> {
    private final List<ModifierProvider> mutators;
    private final T modifier;

    public RegistrationImpl(List<ModifierProvider> mutators, T modifier) {
        this.mutators = mutators;
        this.modifier = modifier;
    }

    @Override
    public List<ModifierProvider> mutators() {
        return Collections.unmodifiableList(mutators);
    }

    @Override
    public T modifier() {
        return modifier;
    }
}
