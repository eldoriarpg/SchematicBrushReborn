/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage.preset;

import de.eldoria.schematicbrush.storage.base.Container;

import java.io.Closeable;

public interface PresetContainer extends Closeable, Container<Preset> {

}
