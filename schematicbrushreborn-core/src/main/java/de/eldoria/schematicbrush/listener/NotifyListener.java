/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotifyListener implements Listener {
    private final Map<UUID, MessageChannel<?>> players = new HashMap<>();
    private final MessageSender messageSender;
    private final Configuration configuration;

    public NotifyListener(Plugin plugin, Configuration configuration) {
        messageSender = MessageSender.getPluginMessageSender(plugin);
        this.configuration = configuration;
    }

    public void setState(Player player, boolean state, MessageChannel<?> channel) {
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
                setState(event.getPlayer(), true, MessageChannel.ACTION_BAR);
            }
        }
    }

    @EventHandler
    public void onPaste(PostPasteEvent event) {
        if (!players.containsKey(event.player().getUniqueId())) return;
        messageSender.send(players.get(event.player().getUniqueId()), MessageType.NORMAL, event.player(),
                "§2Pasted §a" + event.schematic().name() + " §2from set §a" + event.schematicSet().selector().descriptor());
    }
}
