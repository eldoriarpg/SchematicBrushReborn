package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.event.PasteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NotifyListener implements Listener {
    private final Set<UUID> players = new HashSet<>();
    private final MessageSender messageSender;

    public NotifyListener(Plugin plugin) {
        messageSender = MessageSender.getPluginMessageSender(plugin);
    }

    public void setState(Player player, boolean state) {
        if (state) {
            players.add(player.getUniqueId());
        } else {
            players.remove(player.getUniqueId());
        }
    }

    public void onPaste(PasteEvent event) {
        if (!players.contains(event.getPlayer().getUniqueId())) return;
        messageSender.sendMessage(event.getPlayer(), "§2Pasted §a" + event.schematic().name());
    }
}
