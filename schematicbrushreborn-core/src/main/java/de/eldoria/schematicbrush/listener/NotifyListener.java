/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.Replacement;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.sections.MessageChannel;
import de.eldoria.schematicbrush.event.PostPasteEvent;
import de.eldoria.schematicbrush.util.Escape;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class NotifyListener implements Listener {
    private final Map<UUID, MessageChannel> players = new HashMap<>();
    private final MessageSender messageSender;
    private final Configuration configuration;

    public NotifyListener(Plugin plugin, Configuration configuration) {
        messageSender = MessageSender.getPluginMessageSender(plugin);
        this.configuration = configuration;
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
        var replacements = new ArrayList<TagResolver>();
        replacements.add(Replacement.create("schematic", Escape.text(event.schematic().name())));
        replacements.add(Replacement.create("set", event.schematicSet().selector().descriptor()));
        var details = new StringJoiner(", ", "(", ")");
        if (event.paste().flip() != null) {
            details.add("<i18n:listener.notify.flip>");
            replacements.add(Replacement.create("flip", event.paste().flip().value()));
        }
        if (event.paste().rotation() != null) {
            details.add("<i18n:listener.notify.rotation>");
            replacements.add(Replacement.create("rotation", event.paste().rotation().value()));
        }
        if (event.paste().offsetSet() != null) {
            details.add("<i18n:listener.notify.setOffset>");
            replacements.add(Replacement.create("set_offset", event.paste().offsetSet().value()));
        }
        if (event.paste().offsetBrush() != null) {
            details.add("<i18n:listener.notify.brushOffset>");
            replacements.add(Replacement.create("brush_offset", event.paste().offsetBrush().value()));
        }
        var message = "<i18n:listener.notify.pasted> " + details;
        var tags = replacements.toArray(TagResolver[]::new);
        var times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(15), Duration.ZERO);
        switch (players.get(event.player().getUniqueId())) {
            case ACTION_BAR -> messageSender.sendActionBar(event.player(), message, tags);
            case CHAT -> messageSender.sendMessage(event.player(), message, tags);
            case TITLE -> messageSender.sendTitle(event.player(), message, "", times, tags);
            case SUB_TITLE -> messageSender.sendTitle(event.player(), "", message, times, tags);
        }
    }
}
