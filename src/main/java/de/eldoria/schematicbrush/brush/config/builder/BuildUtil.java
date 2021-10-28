package de.eldoria.schematicbrush.brush.config.builder;

import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.Nameable;
import de.eldoria.schematicbrush.brush.config.SettingProvider;
import de.eldoria.schematicbrush.util.Colors;

import java.util.List;
import java.util.stream.Collectors;

public class BuildUtil {
    public static String buildModifier(String baseCommand, Nameable type, List<? extends SettingProvider<?>> provider, Mutator<?> current) {
        String types;
        if (provider.size() > 1) {
            types = provider.stream()
                    .map(SettingProvider::name)
                    .map(name -> String.format("<click:suggest_command:'%s %s %s '>[%s]</click>", baseCommand, type.name(), name, name))
                    .collect(Collectors.joining(", "));
        } else {
            types = String.format("<click:suggest_command:'%s %s %s '>[Change]</click>", baseCommand, type.name(), provider.get(0));
        }
        return String.format("<%s>%s: %s\n%s", Colors.HEADING, type.name(), types, current.asComponent());
    }

}
