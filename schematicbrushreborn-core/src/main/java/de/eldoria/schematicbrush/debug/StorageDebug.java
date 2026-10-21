/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.debug;

import de.eldoria.eldoutilities.debug.DebugDataProvider;
import de.eldoria.eldoutilities.debug.HealthCheck;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reports the storage types and checks that the configured one is available.
 */
public class StorageDebug implements DebugDataProvider {
    private final StorageRegistry registry;
    private final Configuration configuration;

    public StorageDebug(StorageRegistry registry, Configuration configuration) {
        this.registry = registry;
        this.configuration = configuration;
    }

    @Override
    public @NotNull EntryData[] getDebugInformations() {
        var entries = new LinkedHashMap<String, String>();
        entries.put("Configured", configuration.general().storageType().name());
        entries.put("Registered", registry.registry().keySet().stream().map(Nameable::name).collect(Collectors.joining(", ")));
        return new EntryData[]{new EntryData("Storage", entries)};
    }

    @Override
    public @NotNull List<HealthCheck> getHealthChecks() {
        var configured = configuration.general().storageType();
        if (!registry.isRegistered(configured)) {
            return List.of(HealthCheck.error("Storage", "The configured storage type %s is not available. Is the add-on providing it installed and enabled?"
                    .formatted(configured.name())));
        }
        return List.of(HealthCheck.ok("Storage", "Using storage type %s.".formatted(configured.name())));
    }
}
