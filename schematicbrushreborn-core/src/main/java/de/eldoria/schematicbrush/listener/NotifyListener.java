/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.sections.MessageChannel;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class NotifyListener implements Listener {
    private final Map<UUID, MessageChannel> players = new HashMap<>();
    private final MessageSender messageSender;
    private final Configuration configuration;
    BukkitAudiences audiences;
    MiniMessage miniMessage = MiniMessage.miniMessage();

    public NotifyListener(Plugin plugin, Configuration configuration) {
        messageSender = MessageSender.getPluginMessageSender(plugin);
        this.configuration = configuration;
        audiences = BukkitAudiences.create(plugin);
    }

    public void setState(Player player, boolean state, MessageChannel channel) {
        if (state) {
            players.put(player.getUniqueId(), channel);
        } else {
            players.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("schematicbrush.brush.use")) {
            if (configuration.general().isShowNameDefault()) {
                setState(event.getPlayer(), true, configuration.general().defaultNameChannel());
            }
        }
    }

    @EventHandler
    public void onPaste(PostPasteEvent event) {
        if (!players.containsKey(event.player().getUniqueId())) return;
        var builder = new StringBuilder()
                .append("<dark_green>Pasted <green>%s <dark_green>from set <green>%s".formatted(event.schematic().name(), event.schematicSet().selector().descriptor()));
        var joiner = new StringJoiner(", ", "(", ")");
        if (event.paste().flip() != null) {
            joiner.add("Flip: %s".formatted(event.paste().flip().value()));
        }
        if (event.paste().rotation() != null) {
            joiner.add("Rotation: %s".formatted(event.paste().rotation().value()));
        }
        if (event.paste().offsetSet() != null) {
            joiner.add("Set Offset: %s".formatted(event.paste().offsetSet().value()));
        }
        if (event.paste().offsetBrush() != null) {
            joiner.add("Brush Offset: %s".formatted(event.paste().offsetBrush().value()));
        }
        switch (players.get(event.player().getUniqueId())) {
            case ACTION_BAR ->
                    audiences.sender(event.player())
                            .sendActionBar(miniMessage.deserialize(builder.append(" ").append(joiner).toString()));
            case CHAT ->
                    audiences.sender(event.player())
                            .sendMessage(miniMessage.deserialize(builder.append(" ").append(joiner).toString()));
            case TITLE ->
                    audiences.sender(event.player())
                            .showTitle(Title.title(miniMessage.deserialize(builder.append(" ").append(joiner).toString()), Component.empty()));
            case SUB_TITLE ->
                    audiences.sender(event.player())
                            .showTitle(Title.title(Component.empty(), miniMessage.deserialize(builder.append(" ").append(joiner).toString())));
        }
    }
}
