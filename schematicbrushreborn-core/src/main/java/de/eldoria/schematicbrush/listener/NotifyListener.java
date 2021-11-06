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

package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.event.PasteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NotifyListener implements Listener {
    private final Set<UUID> players = new HashSet<>();
    private final MessageSender messageSender;
    private final Configuration configuration;

    public NotifyListener(Plugin plugin, Configuration configuration) {
        messageSender = MessageSender.getPluginMessageSender(plugin);
        this.configuration = configuration;
    }

    public void setState(Player player, boolean state) {
        if (state) {
            players.add(player.getUniqueId());
        } else {
            players.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("schematicbrush.brush.use")) {
            if (configuration.general().isShowNameDefault()) {
                setState(event.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onPaste(PasteEvent event) {
        if (!players.contains(event.player().getUniqueId())) return;
        messageSender.send(MessageChannel.ACTION_BAR, MessageType.NORMAL, event.player(), "§2Pasted §a" + event.schematic().name());
    }
}
