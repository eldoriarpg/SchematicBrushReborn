/*
 *     Schematic Brush Reborn - A World Edit Brush Extension
 *     Copyright (C) 2021 EldoriaRPG Team und Contributor
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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

public final class BuildUtil {
    private BuildUtil() {
        throw new UnsupportedOperationException("This is a utility class.");
    }

    public static String buildModifier(Permissible permissible, String baseCommand, BaseModifier type, List<? extends SettingProvider<?>> provider, Mutator<?> current) {
        String types;
        var filteredProvider = provider.stream().filter(set -> set.hasPermission(permissible)).collect(Collectors.toList());
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
