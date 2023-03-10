/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config;

import de.eldoria.schematicbrush.brush.config.modifier.PlacementModifier;
import de.eldoria.schematicbrush.brush.config.provider.ModifierProvider;

import java.util.List;

public class PlacementModifierRegistrationImpl extends RegistrationImpl<PlacementModifier> implements PlacementModifierRegistration {
    public PlacementModifierRegistrationImpl(List<ModifierProvider> mutators, PlacementModifier modifier) {
        super(mutators, modifier);
    }
}
