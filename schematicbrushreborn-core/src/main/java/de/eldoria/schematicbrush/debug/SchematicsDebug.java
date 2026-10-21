/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.debug;

import de.eldoria.eldoutilities.debug.DebugDataProvider;
import de.eldoria.eldoutilities.debug.HealthCheck;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.sections.SchematicSource;
import de.eldoria.schematicbrush.schematics.SchematicCache;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Reports the loaded schematics and checks that the configured schematic folders exist and contain schematics.
 */
public class SchematicsDebug implements DebugDataProvider {
    private final Plugin plugin;
    private final SchematicRegistry registry;
    private final Configuration configuration;

    public SchematicsDebug(Plugin plugin, SchematicRegistry registry, Configuration configuration) {
        this.plugin = plugin;
        this.registry = registry;
        this.configuration = configuration;
    }

    @Override
    public @NotNull EntryData[] getDebugInformations() {
        var entries = new LinkedHashMap<String, String>();
        entries.put("Schematics", String.valueOf(registry.schematicCount()));
        entries.put("Directories", String.valueOf(registry.directoryCount()));
        registry.registry().forEach((name, cache) -> entries.put("Cache " + name.name(), cache.isReady() ? "ready" : "loading"));
        for (var source : configuration.schematicConfig().sources()) {
            entries.put("Source " + source.path(), Files.isDirectory(resolve(source)) ? "found" : "missing");
        }
        return new EntryData[]{new EntryData("Schematics", entries)};
    }

    @Override
    public @NotNull List<HealthCheck> getHealthChecks() {
        var checks = new ArrayList<HealthCheck>();
        for (var source : configuration.schematicConfig().sources()) {
            var path = resolve(source);
            if (!Files.isDirectory(path)) {
                checks.add(HealthCheck.warn("Schematic source", "The schematic folder %s does not exist.".formatted(path)));
            }
        }
        var ready = registry.registry().values().stream().allMatch(SchematicCache::isReady);
        if (ready && registry.schematicCount() == 0) {
            checks.add(HealthCheck.error("Schematics", "No schematics are loaded. Check the schematic sources in the config."));
        } else {
            checks.add(HealthCheck.ok("Schematics", "%d schematics in %d directories%s.".formatted(
                    registry.schematicCount(), registry.directoryCount(), ready ? "" : ", still loading")));
        }
        return checks;
    }

    private Path resolve(SchematicSource source) {
        var path = Path.of(source.path());
        return source.isRelative() ? plugin.getDataFolder().toPath().getParent().resolve(path) : path;
    }
}
