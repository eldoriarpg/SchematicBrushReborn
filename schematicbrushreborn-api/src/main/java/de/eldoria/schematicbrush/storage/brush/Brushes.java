/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.brush;

import de.eldoria.schematicbrush.storage.base.ContainerHolder;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;

/**
 * A schematic registry to manage {@link PresetContainer}
 */
public interface Brushes extends ContainerHolder<Brush, BrushContainer> {
}
