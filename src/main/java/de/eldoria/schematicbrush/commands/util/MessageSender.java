package de.eldoria.schematicbrush.commands.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class MessageSender {
    private final String PREFIX = "§6[SB] ";
    private final String DEFAULT_MESSAGE_COLOR = "§r§2";
    private final String DEFAULT_ERROR_COLOR = "§r§c";

    /**
     * Send a message to a player
     * @param player receiver of the message
     * @param message message with optinal color codes
     */
    public void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_MESSAGE_COLOR));
    }

    /**
     * Sends a error to a player
     * @param player receiver of the message
     * @param message message with optinal color codes
     */
    public void sendError(Player player, String message) {
        player.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_ERROR_COLOR));
    }
}
