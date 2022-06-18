/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.storage.base.StorageHolder;

public interface StorageRegistry extends StorageHolder<Storage> {
    Nameable YAML = Nameable.of("yaml");
}
