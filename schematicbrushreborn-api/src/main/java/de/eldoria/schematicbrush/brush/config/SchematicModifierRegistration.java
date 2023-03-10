/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.modifier.SchematicModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;

import java.util.List;

/**
 * Represents the registrations for a {@link SchematicModifier}
 */
public interface SchematicModifierRegistration extends Registration<SchematicModifier, List<ModifierProvider>> {
}
