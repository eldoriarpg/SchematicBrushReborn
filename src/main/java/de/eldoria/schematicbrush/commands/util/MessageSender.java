package de.eldoria.schematicbrush.commands.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class MessageSender {
    private final String PREFIX = "§6[SB] ";
    private final String DEFAULT_MESSAGE_COLOR = "§r§2";
    private final String DEFAULT_ERROR_COLOR = "§r§c";

    public void sendMessage(Player p, String message) {
        p.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_MESSAGE_COLOR));
    }

    public void sendError(Player p, String message) {
        p.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_ERROR_COLOR));
    }
}
