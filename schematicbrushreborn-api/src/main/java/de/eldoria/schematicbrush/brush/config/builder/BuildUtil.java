package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.provider.SettingProvider;
import de.eldoria.schematicbrush.brush.config.util.ComponentProvider;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.util.Colors;

import java.util.List;
import java.util.stream.Collectors;

public class BuildUtil {
    public static String buildModifier(String baseCommand, Nameable type, List<? extends SettingProvider<?>> provider, Mutator<?> current) {
        String types;
        if (provider.size() > 1) {
            types = provider.stream()
                    .map(p -> String.format("<click:%s:'%s %s %s '>[%s]</click>", p.commandType(), baseCommand, type.name(), p.name(), p.name()))
                    .collect(Collectors.joining(", "));
        } else {
            types = String.format("<click:%s:'%s %s %s '>[Change]</click>", provider.get(0).commandType(), baseCommand, type.name(), provider.get(0).name());
        }
        return String.format("<%s>%s: <%s>%s\n  %s", Colors.HEADING, type.name(), Colors.CHANGE, types, provider.size() > 1 ? renderProvider(current) : renderSingleProvider(current));
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
