package de.eldoria.schematicbrush;

import org.bukkit.entity.Player;

public class MessageSender {
    public static void sendMessage(Player p, String message) {
        p.sendMessage("§6[SB] §4" + message);
    }
    public static void sendError(Player p, String message) {
        p.sendMessage("§6[SB] §c" + message);
    }
}
