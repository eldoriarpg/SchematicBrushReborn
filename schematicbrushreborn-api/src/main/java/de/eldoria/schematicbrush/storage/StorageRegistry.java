/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.storage.base.StorageHolder;

/**
 * An interface which represents a storage registry.
 */
public interface StorageRegistry extends StorageHolder<Storage> {
    /**
     * The default storage method which should be always available.
     */
    Nameable YAML = Nameable.of("yaml");
}
