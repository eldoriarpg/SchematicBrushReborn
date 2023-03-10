/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;
import de.eldoria.schematicbrush.registry.Registry;

import java.util.List;

/**
 * Represents a {@link Registration} at a {@link Registry} or similar pattern,
 * which allows to register {@link PlacementModifier} with multiple {@link ModifierProvider}s
 */
public interface PlacementModifierRegistration extends Registration<PlacementModifier, List<ModifierProvider>> {
}
