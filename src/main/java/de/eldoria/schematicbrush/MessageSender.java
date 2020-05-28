package de.eldoria.schematicbrush;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class MessageSender {
    public void sendMessage(Player p, String message) {
        p.sendMessage("§6[SB] §4" + message);
    }
    public void sendError(Player p, String message) {
        p.sendMessage("§6[SB] §c" + message);
    }
}
