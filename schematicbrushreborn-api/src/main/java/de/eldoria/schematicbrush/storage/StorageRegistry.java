/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.storage;

import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.config.sections.GeneralConfig;
import de.eldoria.schematicbrush.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * A class which represents a storage registry.
 */
public interface StorageRegistry extends Registry<Nameable, Storage> {
    /**
     * The default storage method which should be always available.
     */
    Nameable YAML = Nameable.of("yaml");

    /**
     * Get the active storage, which is defined in the {@link GeneralConfig#storageType()} or a fallback {@link Storage}.
     *
     * @return storage
     */
    @NotNull Storage activeStorage();

    /**
     * Migrate a source storage into a target storage
     *
     * @param source source
     * @param target target
     * @return Returns a future which completes once all underlying processes complete
     */
    CompletableFuture<Void> migrate(Nameable source, Nameable target);
}
