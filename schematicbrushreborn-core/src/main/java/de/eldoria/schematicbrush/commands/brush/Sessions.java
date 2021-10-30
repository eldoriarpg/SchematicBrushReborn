package de.eldoria.schematicbrush.commands.brush;

import de.eldoria.eldoutilities.localization.MessageComposer;
import de.eldoria.messageblocker.blocker.IMessageBlockerService;
import de.eldoria.schematicbrush.brush.config.BrushSettingsRegistry;
import de.eldoria.schematicbrush.brush.config.builder.BrushBuilder;
import de.eldoria.schematicbrush.brush.config.builder.BuildUtil;
import de.eldoria.schematicbrush.schematics.SchematicRegistry;
import de.eldoria.schematicbrush.util.Colors;
import de.eldoria.schematicbrush.util.WorldEditBrush;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static de.eldoria.schematicbrush.brush.config.builder.BuildUtil.buildModifier;

public class Sessions {
    private final MiniMessage miniMessage = MiniMessage.get();
    private final BukkitAudiences audiences;
    private final BrushSettingsRegistry registry;
    private final SchematicRegistry schematicRegistry;
    private final IMessageBlockerService messageBlocker;
    private final Map<UUID, BrushBuilder> session = new HashMap<>();

    public Sessions(Plugin plugin, BrushSettingsRegistry registry, SchematicRegistry schematicRegistry, IMessageBlockerService messageBlocker) {
        this.registry = registry;
        audiences = BukkitAudiences.create(plugin);
        this.schematicRegistry = schematicRegistry;
        this.messageBlocker = messageBlocker;
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
                .map(brush -> brush.toBuilder(registry, schematicRegistry))
                .orElse(new BrushBuilder(player, registry, schematicRegistry));
    }

    public void showBrush(Player player) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);
        var count = new AtomicInteger(0);
        var sets = String.format("<%s>Schematic Sets: <click:run_command:'/sbr addSet'><%s>[Add]</click>", Colors.HEADING, Colors.ADD);
        sets += String.format("  <click:suggest_command:'/sbr addpreset '><%s>[Add Preset]</click>%n", Colors.ADD);

        sets += builder.schematicSets().stream()
                .map(set -> String.format("<%s><hover:show_text:'%s'>%s</hover> <%s><click:run_command:'/sbr showSet %s'>[Edit]</click> <%s><click:run_command:'/sbr removeSet %s'>[Remove]</click>",
                        Colors.NAME, set.infoComponent(), BuildUtil.renderProvider(set.selector()), Colors.CHANGE, count.get(), Colors.REMOVE, count.getAndIncrement()))
                .collect(Collectors.joining("\n"));
        var mutatorMap = builder.placementModifier();
        var modifierStrings = new ArrayList<String>();
        for (var entry : registry.placementModifier().entrySet()) {
            modifierStrings.add(buildModifier("/sbr modify", entry.getKey(), entry.getValue(), mutatorMap.get(entry.getKey())));
        }
        var modifier = String.join("\n", modifierStrings);
        var panel = String.format("%s\n%s", sets, modifier);
        var buttons = String.format("<click:run_command:'/sbr bind'><%s>[Bind]</click> " +
                                    "<click:run_command:'/sbr clear'><%s>[Clear]</click> " +
                                    "<click:suggest_command:'/sbr savepreset '><%s>[Save]</click>", Colors.ADD, Colors.REMOVE, Colors.CHANGE);
        var composer = MessageComposer.create().text(panel).newLine().text(buttons).prependLines(20);
        messageBlocker.ifEnabled(composer, c -> c.newLine().text("<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(composer.build()));
    }

    public void showSet(Player player, int id) {
        messageBlocker.blockPlayer(player);
        var builder = getOrCreateSession(player);
        var optSet = builder.getSchematicSet(id);
        if (optSet.isEmpty()) {
            audiences.player(player).sendMessage(miniMessage.parse("[SBR] Invalid set."));
            return;
        }

        var set = optSet.get();
        var s = set.interactComponent(registry, id);

        var buttons = "<click:run_command:'/sbr show'>[Back]</click>";
        var message = String.join("\n", s, buttons);

        message = messageBlocker.ifEnabled(message, m -> m + String.format("%n<click:run_command:'/sbrs chatblock false'><%s>[x]</click>", Colors.REMOVE));
        messageBlocker.announce(player, "[x]");
        audiences.player(player).sendMessage(miniMessage.parse(MessageComposer.create().text(message).prependLines(20).build()));
    }
}
