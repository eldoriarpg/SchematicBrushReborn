package de.eldoria.schematicbrush.listener;

import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.schematicbrush.config.Config;
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
    private final Config config;

    public NotifyListener(Plugin plugin, Config config) {
        messageSender = MessageSender.getPluginMessageSender(plugin);
        this.config = config;
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
            if (config.getGeneral().isShowNameDefault()) {
                setState(event.getPlayer(), true);
            }
        }
    }

    @EventHandler
    public void onPaste(PasteEvent event) {
        if (!players.contains(event.player().getUniqueId())) return;
        messageSender.sendMessage(event.player(), "§2Pasted §a" + event.schematic().name());
    }
}
