/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.debug;

import de.eldoria.eldoutilities.debug.DebugDataProvider;
import de.eldoria.eldoutilities.debug.HealthCheck;
import de.eldoria.eldoutilities.debug.data.EntryData;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Reports the installed add-ons and checks that each of them is enabled. An add-on that is installed but disabled
 * usually failed to load, often because it was built for another version of Schematic Brush.
 */
public class AddOnDebug implements DebugDataProvider {
    private final Plugin plugin;

    public AddOnDebug(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull EntryData[] getDebugInformations() {
        var entries = new LinkedHashMap<String, String>();
        for (var addOn : addOns()) {
            entries.put(addOn.getName(), "%s (%s)".formatted(addOn.getPluginMeta().getVersion(), addOn.isEnabled() ? "enabled" : "disabled"));
        }
        return new EntryData[]{new EntryData("Add-ons", entries)};
    }

    @Override
    public @NotNull List<HealthCheck> getHealthChecks() {
        var addOns = addOns();
        var disabled = addOns.stream()
                             .filter(addOn -> !addOn.isEnabled())
                             .map(addOn -> HealthCheck.error("Add-on " + addOn.getName(),
                                     "%s %s is installed but not enabled. Check the log for errors and whether it supports %s %s."
                                             .formatted(addOn.getName(), addOn.getPluginMeta().getVersion(), plugin.getName(),
                                                     plugin.getPluginMeta().getVersion())))
                             .toList();
        if (!disabled.isEmpty()) return disabled;
        if (addOns.isEmpty()) return List.of();
        return List.of(HealthCheck.ok("Add-ons", "%d add-ons enabled.".formatted(addOns.size())));
    }

    private List<Plugin> addOns() {
        var name = plugin.getName();
        return Arrays.stream(plugin.getServer().getPluginManager().getPlugins())
                     .filter(other -> other.getPluginMeta().getPluginDependencies().contains(name)
                                      || other.getPluginMeta().getPluginSoftDependencies().contains(name))
                     .toList();
    }
}
