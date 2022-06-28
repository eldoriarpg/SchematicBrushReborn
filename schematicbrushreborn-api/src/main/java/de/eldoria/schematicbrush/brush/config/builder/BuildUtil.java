/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.config.modifier.BaseModifier;
import de.eldoria.schematicbrush.brush.config.provider.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.SettingProvider;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.util.Colors;
import org.bukkit.permissions.Permissible;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class providing utility methods for building text UIs.
 */
public final class BuildUtil {
    private BuildUtil() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static String buildModifier(Permissible permissible, String baseCommand, BaseModifier type, List<? extends SettingProvider<?>> provider, Mutator<?> current) {
        if (current == null) current = (Mutator<?>) provider.get(0).defaultSetting();
        String types;
        var filteredProvider = provider.stream().filter(set -> set.hasPermission(permissible)).toList();
        if (filteredProvider.size() > 1) {
            types = filteredProvider.stream()
                    .map(p -> String.format("<click:%s:'%s %s %s '><hover:show_text:'<%s>%s'>[%s]</hover></click>",
                            p.commandType(), baseCommand, type.name(), p.name(), Colors.NEUTRAL, p.description(), p.name()))
                    .collect(Collectors.joining(" "));
        } else {
            types = String.format("<click:%s:'%s %s %s '>[Change]</click>",
                    filteredProvider.get(0).commandType(), baseCommand, type.name(), filteredProvider.get(0).name());
        }
        return String.format("<hover:show_text:'<%s>%s'><%s>%s:</hover> <%s>%s\n  %s",
                Colors.NEUTRAL, type.description(), Colors.HEADING, type.name(), Colors.CHANGE, types,
                filteredProvider.size() > 1 ? renderProvider(current) : renderSingleProvider(current));
    }

    public static String renderProvider(ComponentProvider provider) {
        return String.format("<%s>%s%s<%s>%s", Colors.NAME, provider.name(),
                provider.descriptor() == null || provider.descriptor().isBlank() ? "" : ": ", Colors.VALUE,
                provider.descriptor() == null || provider.descriptor().isBlank() ? "" : provider.descriptor());
    }

    public static String renderSingleProvider(ComponentProvider provider) {
        return String.format("<%s>%s", Colors.VALUE, provider.descriptor());
    }
}
