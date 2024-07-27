/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
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

    public static String buildModifier(Permissible permissible, String baseCommand, String removeBaseCommand, BaseModifier type, List<? extends SettingProvider<?>> provider, Mutator<?> current) {
        if (current == null) current = (Mutator<?>) provider.get(0).defaultSetting();
        String types;
        var filteredProvider = provider.stream().filter(set -> set.hasPermission(permissible)).toList();
        if (filteredProvider.size() > 1) {
            types = filteredProvider.stream()
                    .map(p -> String.format("<click:%s:'%s %s %s '><hover:show_text:'<neutral>%s'>[%s]</hover></click>",
                            p.commandType(), baseCommand, type.name(), p.name(), p.localizedDescription(), p.localizedName()))
                    .collect(Collectors.joining(" "));
        } else {
            types = String.format("<click:%s:'%s %s %s '>[<i18n:words.change>]</click>",
                    filteredProvider.get(0).commandType(), baseCommand, type.name(), filteredProvider.get(0).name());
        }

        var remove = "";

        if (!type.required()) {
            remove = String.format("<remove><click:run_command:'%s %s'>[<i18n:words.remove>]</click>", removeBaseCommand, type.name());
        }

        return String.format("<hover:show_text:'<neutral>%s'><heading>%s:</hover> <change>%s %s\n  %s",
                type.description(), type.getLocalizedName(), types, remove,
                filteredProvider.size() > 1 ? renderProvider(current) : renderSingleProvider(current));
    }

    public static String renderProvider(ComponentProvider provider) {
        return String.format("<name>%s%s<value>%s", provider.localizedName(),
                provider.localizedDescriptor() == null || provider.localizedDescriptor().isBlank() ? "" : ": ",
                provider.localizedDescriptor() == null || provider.localizedDescriptor().isBlank() ? "" : provider.localizedDescriptor());
    }

    public static String renderSingleProvider(ComponentProvider provider) {
        return String.format("<value>%s", provider.localizedDescriptor());
    }
}
