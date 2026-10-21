/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */

package de.eldoria.schematicbrush.editor;

import de.eldoria.schematicbrush.config.Configuration;
import de.eldoria.schematicbrush.config.sections.EditorType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The editor each player sees.
 * <p>
 * A player's choice lasts until they leave; until then the configured default applies. Bedrock players connected
 * through Floodgate always get the chat editor, since their client cannot show dialogs.
 */
public final class EditorPreferences implements Listener {
    private final Configuration configuration;
    private final boolean floodgate;
    private final Map<UUID, EditorType> chosen = new HashMap<>();

    public EditorPreferences(Plugin plugin, Configuration configuration) {
        this.configuration = configuration;
        floodgate = plugin.getServer().getPluginManager().isPluginEnabled("floodgate");
    }

    /**
     * The editor to show a player.
     *
     * @param player player
     * @return editor type
     */
    public EditorType editor(Player player) {
        if (floodgate && Floodgate.isBedrock(player)) return EditorType.CHAT;
        return chosen.getOrDefault(player.getUniqueId(), configuration.general().editor());
    }

    /**
     * Sets the editor a player sees until they leave.
     *
     * @param player player
     * @param editor editor type
     */
    public void choose(Player player, EditorType editor) {
        chosen.put(player.getUniqueId(), editor);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        chosen.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Holds every reference to the Floodgate API, so it is only loaded when Floodgate is installed.
     */
    private static final class Floodgate {
        private static boolean isBedrock(Player player) {
            return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
        }
    }
}
