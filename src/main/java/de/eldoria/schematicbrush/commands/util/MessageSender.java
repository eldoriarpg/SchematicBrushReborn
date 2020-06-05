package de.eldoria.schematicbrush.commands.util;

import org.bukkit.entity.Player;

public final class MessageSender {
    private static final String PREFIX = "§6[SB] ";
    private static final String DEFAULT_MESSAGE_COLOR = "§r§2";
    private static final String DEFAULT_ERROR_COLOR = "§r§c";

    private MessageSender() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Send a message to a player
     * @param player receiver of the message
     * @param message message with optinal color codes
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_MESSAGE_COLOR));
    }

    /**
     * Sends a error to a player
     * @param player receiver of the message
     * @param message message with optinal color codes
     */
    public static void sendError(Player player, String message) {
        player.sendMessage(PREFIX + DEFAULT_MESSAGE_COLOR + message.replaceAll("§r", DEFAULT_ERROR_COLOR));
    }
}
