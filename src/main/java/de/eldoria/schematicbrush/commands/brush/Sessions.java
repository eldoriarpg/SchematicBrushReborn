package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.Mutator;
import de.eldoria.schematicbrush.brush.config.Nameable;
import de.eldoria.schematicbrush.brush.config.SettingProvider;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.util.Colors;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Sessions {
    private final MiniMessage miniMessage = MiniMessage.get();
    private final BukkitAudiences audiences;
    private final BrushSettingsRegistry registry;
    private final Map<UUID, BrushBuilder> session = new HashMap<>();

    public Sessions(Plugin plugin, BrushSettingsRegistry registry) {
        this.registry = registry;
        audiences = BukkitAudiences.create(plugin);
    }

    public void setSession(Player player, BrushBuilder builder) {
        session.put(player.getUniqueId(), builder);
    }

    public BrushBuilder getOrCreateSession(Player player) {
        return session.computeIfAbsent(player.getUniqueId(), key -> getOrCreateBuilder(player));
    }

    public void startSession(Player player) {
        session.put(player.getUniqueId(), getOrCreateBuilder(player));
    }

    private BrushBuilder getOrCreateBuilder(Player player) {
        return WorldEditBrush.getSchematicBrush(player)
                .map(brush -> brush.toBuilder(registry))
                .orElse(new BrushBuilder(registry));
    }

    public void showBrush(Player player) {
        var builder = getOrCreateSession(player);
        var count = new AtomicInteger(0);
        var sets = String.format("Schematic Sets:<click:run_command:'/sbr addSet'><%s>[Add]</click>%n", Colors.ADD) + builder.schematicSets().stream()
                .map(set -> String.format("%s <%s><click:run_command:'sbr showSet %s'>[Edit]</click> <%s><click:run_command:'/sbr removeSet %s'>[Remove]</click>",
                        set.selector().asComponent(), Colors.CHANGE, count.getAndIncrement(), Colors.REMOVE, count.get()))
                .collect(Collectors.joining("\n"));
        var mutatorMap = builder.placementModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : registry.placementModifier().entrySet()) {
            var type = entry.getValue().stream()
                    .map(SettingProvider::name)
                    .map(name -> String.format("<click:suggest_command:'/sbr modify %s %s '>[%s]</click>", entry.getKey().name(), name, name))
                    .collect(Collectors.joining(", "));

            var format = String.format("<%s>%s: %s\n%s", Colors.HEADING, entry.getKey().name(), type, mutatorMap.get(entry.getKey()).asComponent());
            modifierStrings.add(format);
            modifierStrings.add(buildModifier("/sbr modify", entry.getKey(), entry.getValue(), mutatorMap.get(entry.getKey())));
        }
        var modifier = String.join("\n", modifierStrings);
        var panel = String.format("%s\n%s", sets, modifier);
        var buttons = "<click:run_command:'/sbr bind'>[Bind]</click> <click:run_command:'/sbr clear'>[Clear]</click>";
        audiences.player(player).sendMessage(miniMessage.parse(panel + "\n" + buttons));
    }

    public void showSet(Player player, int id) {
        var builder = getOrCreateSession(player);
        var optSet = builder.getSchematicSet(id);
        if (optSet.isEmpty()) {
            audiences.player(player).sendMessage(miniMessage.parse("[SBR] Invalid set."));
            return;
        }

        var set = optSet.get();

        var selector = "Selector:" + registry.selector().stream()
                .map(SettingProvider::name)
                .map(sel -> String.format("<click:suggest_command:'/sbr modify selector %s '>[%s]</click>", sel, sel))
                .collect(Collectors.joining(", "));
        selector += "\n" + set.selector().asComponent();

        var mutatorMap = set.schematicModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : registry.schematicModifier().entrySet()) {
            modifierStrings.add(buildModifier("/sbr modifyset " + id, entry.getKey(), entry.getValue(), mutatorMap.get(entry.getKey())));
        }
        var modifier = String.join("\n", modifierStrings);
        var weight = String.format("<%s>Weight: <%s>%s <click:suggest_command:'/sbr modifyset weight '><%s>[change]</click>", Colors.NAME, Colors.VALUE, set.weight(), Colors.CHANGE);
        var buttons = "<click:run_command:'/sbr show'>[Back]</click>";
    }

    private String buildModifier(String baseCommand, Nameable type, List<? extends SettingProvider<?>> provider, Mutator<?> current) {
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
